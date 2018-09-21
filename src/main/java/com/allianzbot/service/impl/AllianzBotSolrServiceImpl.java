package com.allianzbot.service.impl;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.allianzbot.dto.AllianzBotDocument;
import com.allianzbot.dto.AllianzBotSentence;
import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.exception.AllianzBotRunTimeException;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;
import com.allianzbot.service.interfaces.IAllianzBotOpenNlpService;
import com.allianzbot.service.interfaces.IAllianzBotSolrService;
import com.allianzbot.utils.AllianzBotConstants;
import com.allianzbot.utils.FileUtils;

import opennlp.tools.util.InvalidFormatException;

/**
 * Service implementation for solr operations.
 * 
 * @author eknath.take
 *
 */
@Service("allianzBotSolrService")
public class AllianzBotSolrServiceImpl implements IAllianzBotSolrService {

	@Value("${url.solr.collection}")
	private String SOLR_COLLECTION1;

	private SolrClient client;

	@Inject
	@Named("allianzBotOpenNlpService")
	private IAllianzBotOpenNlpService allianzBotOpenNlpService;

	private Logger log = LoggerFactory.getLogger(AllianzBotSolrServiceImpl.class);

	@Override
	public UpdateResponse storeDocument(AllianzBotDocument allianzBotServiceResponse)
			throws SolrServerException, IOException {

		SolrInputDocument doc = null;
		Object content = allianzBotServiceResponse.getContent();
		if (content instanceof LinkedHashMap) {

			// in following HashMap Key is Row number and values are all the
			// Columns
			LinkedHashMap<Integer, List<String>> document = (LinkedHashMap<Integer, List<String>>) content;
			// iterate through all the columns
			int i = 0;
			for (Map.Entry<Integer, List<String>> entry : document.entrySet()) {
				List<String> columns = entry.getValue();
				if (columns.size() == 2 && CollectionUtils.isNotEmpty(columns)) {
					doc = new SolrInputDocument();
					doc.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_LIKES, new Double(0.0));
					doc.addField(AllianzBotConstants.AB_SOLR_FIELD_ID,
							DigestUtils.md5Hex(columns.get(0) + columns.get(1)));
					doc.addField(AllianzBotConstants.AB_SOLR_FIELD_QUESTION, columns.get(0));
					doc.addField(AllianzBotConstants.AB_SOLR_FIELD_ANSWER, columns.get(1));

					client.add(doc);
					if (i % 100 == 0)
						client.commit(); // periodically flush
					i++;
				}
			}

		} else {
			// Indexing Solr Document
			doc = new SolrInputDocument();
			doc.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_LIKES, new Double(0.0));
			doc.addField(AllianzBotConstants.AB_SOLR_FIELD_ID, DigestUtils.md5Hex((String) content));
			doc.addField(AllianzBotConstants.AB_SOLR_FIELD_CONTENT, (String) content);
			client.add(doc);
		}
		return client.commit();
	}

	@Override
	public AllianzBotSolrSearchDocumentResponse searchDocuments(String query)
			throws SolrServerException, IOException, AllianzBotException {

		// prepare for query and keywords
		final String solrSearchQuery = buildQueryAndKeywords(query);

		// Preparing to Solr query
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(solrSearchQuery).setStart(0).setRows(AllianzBotConstants.AB_MAX_ROWS).setFields(
				AllianzBotConstants.AB_SOLR_FIELD_ID, AllianzBotConstants.AB_SOLR_FIELD_CONTENT,
				AllianzBotConstants.AB_SOLR_FIELD_QUESTION, AllianzBotConstants.AB_SOLR_FIELD_ANSWER,
				AllianzBotConstants.SOLR_FIELD_SCORE, AllianzBotConstants.AB_SOLR_FIELD_TOTAL_LIKES);

		log.info("Solr Query:{}", solrQuery);
		SolrDocumentList documents = client.query(solrQuery).getResults();

		return mapToAllianzBotSolrSearchDocumentResponse(query, documents);
	}

	/**
	 * This method will prepare for user query and keywords. The query will be
	 * prepared in following format: content:search term OR question:search term The
	 * keywords will be generated followed by space.
	 * 
	 * @param queryMap
	 * @return The List, the query and keywords will be returned in list. In the
	 *         list the keywords can be found at 0 th index and query will be at 1
	 *         st index.
	 * @throws AllianzBotException
	 */
	private String buildQueryAndKeywords(String query) throws AllianzBotException {
		if (StringUtils.isNotEmpty(query)) {
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append(AllianzBotConstants.AB_SOLR_FIELD_CONTENT).append(AllianzBotConstants.AB_COLON)
					.append(query).append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_SOLR_FIELD_QUESTION)
					.append(AllianzBotConstants.AB_COLON).append(query);
			return queryBuilder.toString();

		} else
			throw new AllianzBotException(HttpStatus.BAD_REQUEST.value(), "Please enter your query.");
	}

	/**
	 * Filter the Searched Documents
	 * 
	 * @param keywords
	 * @param documents
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws AllianzBotException
	 */
	private AllianzBotSolrSearchDocumentResponse mapToAllianzBotSolrSearchDocumentResponse(final String keywords,
			SolrDocumentList documents) throws InvalidFormatException, IOException, AllianzBotException {

		AllianzBotSolrSearchDocumentResponse allianzBotSolrServiceResponse = new AllianzBotSolrSearchDocumentResponse();
		Set<AllianzBotSentence> finalResult = new TreeSet<>(new AllianzBotScoreAndHitsComparator());
		SortedMap<Double, AllianzBotSentence> result = new TreeMap<Double, AllianzBotSentence>(
				new AllianzBotScoreAndHitsComparator1());
		// tokenize to query
		List<String> tokensOfTheQuery = Stream.of(allianzBotOpenNlpService.tokenize(keywords)).map(s -> s.toLowerCase())
				.collect(Collectors.toList());
		for (int x = 0; x < documents.size(); x++) {
			final List<String> listAnswers = ((List<String>) documents.get(x)
					.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_ANSWER));
			final List<String> listQuestions = ((List<String>) documents.get(x)
					.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_QUESTION));
			final String id = documents.get(x).getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_ID).toString();
			final List<Double> likes = ((List<Double>) documents.get(x)
					.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_LIKES));
			final Double score = new Double(
					documents.get(x).getFieldValue(AllianzBotConstants.SOLR_FIELD_SCORE).toString());

			// log.info("-------------ANSWER------------------");
			if (CollectionUtils.isNotEmpty(listAnswers) && CollectionUtils.isNotEmpty(listQuestions)
					&& CollectionUtils.isNotEmpty(likes)) {
				AllianzBotSentence mapToAllianzBotSentence = mapToAllianzBotSentence(id, listQuestions.get(0),
						listAnswers.get(0), score, likes.get(0));
				if (null != mapToAllianzBotSentence)
					result.put(Double.sum(mapToAllianzBotSentence.getScore(), mapToAllianzBotSentence.getLikes()),
							mapToAllianzBotSentence);
			}

			// log.info("-------------CONTENT------------------");
			@SuppressWarnings("unchecked")
			List<String> contents = (List<String>) documents.get(x).get(AllianzBotConstants.AB_SOLR_FIELD_CONTENT);
			if (CollectionUtils.isNotEmpty(contents)) {
				String content = contents.get(0);
				try {
					// all the sentences
					List<AllianzBotSentence> sentences = allianzBotOpenNlpService.sentenceDetect(content);

					// Iterate through all the sentences
					for (AllianzBotSentence allianzBotSentence : sentences) {
						if (null != allianzBotSentence) {
							String[] tokensOfTheSentences = allianzBotOpenNlpService
									.tokenize(FileUtils.removeStopWords(allianzBotSentence.getAnswer()));

							// calculating the features
							final double features = Stream.of(tokensOfTheSentences)
									.filter(token -> tokensOfTheQuery.contains(token.toLowerCase())).count();
							final double calculaterScore = calculateScore(features, tokensOfTheSentences.length);
							AllianzBotSentence allianzBotNewSentence = mapToAllianzBotSentence(id, keywords,
									allianzBotSentence.getAnswer(), calculaterScore, allianzBotSentence.getLikes());
							if (null != allianzBotNewSentence)
								result.put(
										Double.sum(allianzBotNewSentence.getScore(), allianzBotNewSentence.getLikes()),
										allianzBotNewSentence);
						}
					}
				} catch (IOException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				} catch (AllianzBotException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				}
			}

		}

		log.info("First: {}", result);
		// remove all the duplicate answers
		SortedMap<String, AllianzBotSentence> newAbs = new TreeMap<String, AllianzBotSentence>(
				new AllianzBotAnswerComparator1());
		// newAbs.putAll(result.entrySet().stream().map(m ->
		// m.getValue()).collect(Collectors.toSet()));
		result.forEach((k, v) -> newAbs.put(v.getAnswer(), v));
		result.clear();

		log.info("Second: {}", newAbs);
		// sort all the answers by the high and score
		newAbs.forEach((k, v) -> result.put(Double.sum(v.getScore(), v.getLikes()), v));
		finalResult.addAll(result.values());
		log.info("Third: {}", result);
		newAbs.clear();

		allianzBotSolrServiceResponse.setDocuments(finalResult);
		return allianzBotSolrServiceResponse;
	}

	private AllianzBotSentence mapToAllianzBotSentence(String id, String question, String answer, double score,
			double likes) {
		AllianzBotSentence allianzBotNewSentence = null;
		if (StringUtils.isNotEmpty(id) && Double.compare(score, 0) > 0) {
			allianzBotNewSentence = new AllianzBotSentence();
			allianzBotNewSentence.setId(id);
			allianzBotNewSentence.setScore(score);
			allianzBotNewSentence.setQuestion(question);
			allianzBotNewSentence.setAnswer(answer);
			allianzBotNewSentence.setLikes(likes);
		}
		return allianzBotNewSentence;

	}

	private double calculateScore(double features, int totalFeatures) {
		return features / totalFeatures;
	}

	@Override
	public void updateScore(AllianzBotSentence document) throws SolrServerException, IOException, AllianzBotException {

		SolrInputDocument solrInputDocument = new SolrInputDocument();
		if (null != document) {
			String sentence = FileUtils.removeStopWords(document.getQuestion().concat(document.getAnswer()));
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_ID, DigestUtils.md5Hex(sentence));
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_LIKES, document.getLikes());
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_QUESTION, document.getQuestion());
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_ANSWER, document.getAnswer());
		}
		client.add(solrInputDocument);
		client.commit();

	}

	@PostConstruct
	public void initSolr() {
		log.info("SOLR server is starting.");
		client = new HttpSolrClient.Builder(SOLR_COLLECTION1).build();
		log.info("SOLR server is started.");
	}

	@PreDestroy
	public void destroySolr() throws IOException {

		client.close();
		log.info("SOLR server is stopped.");
	}
}

/**
 * Comparator to compare AllianzBotSentence by answer. This will removes the
 * duplicate answers as well.
 * 
 * @author eknath.take
 *
 */
class AllianzBotAnswerComparator implements Comparator<AllianzBotSentence> {

	@Override
	public int compare(AllianzBotSentence abs1, AllianzBotSentence abs2) {
		return abs1.getAnswer().compareTo(abs2.getAnswer());
	}
}

/**
 * Comparator to compare AllianzBotSentence by answer+score of the document.\
 * 
 * @author eknath.take
 *
 */
class AllianzBotScoreAndHitsComparator implements Comparator<AllianzBotSentence> {

	@Override
	public int compare(AllianzBotSentence abs1, AllianzBotSentence abs2) {
		final double thisScore = Double.sum(abs1.getScore(), abs1.getLikes());
		final double allianzBotScore = Double.sum(abs2.getScore(), abs2.getLikes());
		if (thisScore == allianzBotScore) {
			return 0;
		} else if (thisScore > allianzBotScore) {
			return -1;
		}
		return 1;
	}
}

/**
 * Comparator to compare AllianzBotSentence by answer+score of the document.\
 * 
 * @author eknath.take
 *
 */
class AllianzBotScoreAndHitsComparator1 implements Comparator<Double> {

	@Override
	public int compare(Double abs1, Double abs2) {
		if (abs1 == abs2) {
			return 0;
		} else if (abs1 > abs2) {
			return -1;
		}
		return 1;
	}
}

/**
 * Comparator to compare AllianzBotSentence by answer. This will removes the
 * duplicate answers as well.
 * 
 * @author eknath.take
 *
 */
class AllianzBotAnswerComparator1 implements Comparator<String> {

	@Override
	public int compare(String abs1, String abs2) {
		return abs1.compareTo(abs2);
	}
}