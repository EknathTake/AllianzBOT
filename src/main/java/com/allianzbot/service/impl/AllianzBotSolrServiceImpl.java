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

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.exception.AllianzBotRunTimeException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.model.AllianzBotSentence;
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
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer, List<String>> document = (LinkedHashMap<Integer, List<String>>) content;
			// iterate through all the columns
			int i = 0;
			for (Map.Entry<Integer, List<String>> entry : document.entrySet()) {
				List<String> columns = entry.getValue();

				if (CollectionUtils.isNotEmpty(columns)) {
					if (columns.size() == 2) {
						/**for training related data*/
						doc = new SolrInputDocument();
						doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES, new Double(0.0));
						doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID,
								DigestUtils.md5Hex(columns.get(0) + columns.get(1)));
						doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION, columns.get(0));
						doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER, columns.get(1));

					} else if (columns.size() == 16) {
						/** for test center data */
						doc = new SolrInputDocument();
						doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES, new Double(0.0));
						doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID, DigestUtils.md5Hex(columns.get(5)));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_TEAM, columns.get(0));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_TEST_CASE_ID, columns.get(1));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_AUTO_STATUS, columns.get(2));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_REQUIREMENTS_ID, columns.get(3));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_RISK_CLASS, columns.get(4));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_TEST_SET_ID, columns.get(5));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_TEST_LAB_PATH, columns.get(6));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_EXECUTION_DATE, columns.get(7));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_EXECUTION_STATUS, columns.get(8));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_FAILED_RUN_COUNT, columns.get(9));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID_STR, columns.get(10));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_FAILED_STEP, columns.get(11));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_FAILED_LOG, columns.get(12));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_SCREEN_SHOT_URL, columns.get(13));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY, columns.get(14));
						doc.addField(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID, columns.get(15));
					}

					client.add(doc);
					if (i % 100 == 0)
						client.commit(); // periodically flush
					i++;
				}
			}

		} else {
			// Indexing Solr Document
			doc = new SolrInputDocument();
			doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES, new Double(0.0));
			doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID, DigestUtils.md5Hex((String) content));
			doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT, (String) content);
			client.add(doc);
		}
		return client.commit();
	}

	@Override
	public AllianzBotSolrSearchDocumentResponse searchDocuments(String query, boolean isSearch)
			throws SolrServerException, IOException, AllianzBotException {

		// prepare for query and keywords
		final String solrSearchQuery = buildQueryAndKeywords(query);

		// Preparing to Solr query
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(solrSearchQuery).setStart(0).setRows(AllianzBotConstants.AB_MAX_ROWS).setFields(
				AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID, AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT,
				AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION, AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER,
				AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_SCORE, AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES,
				AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY, AllianzBotConstants.ABTestCenter.AB_FAILED_LOG);

		log.info("Solr Query:{}", solrQuery);
		SolrDocumentList documents = client.query(solrQuery).getResults();

		return prepareForAllianzBotSolrSearchDocumentResponse(query, documents, isSearch);
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
			queryBuilder.append(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT).append(AllianzBotConstants.AB_COLON)
					.append(query).append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION)
					.append(AllianzBotConstants.AB_COLON).append(query)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.ABTestCenter.AB_FAILED_LOG)
					.append(AllianzBotConstants.AB_COLON).append(AllianzBotConstants.AB_STAR).append(query)
					.append(AllianzBotConstants.AB_STAR);
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
	private AllianzBotSolrSearchDocumentResponse prepareForAllianzBotSolrSearchDocumentResponse(final String keywords,
			SolrDocumentList documents, boolean isSearch)
			throws InvalidFormatException, IOException, AllianzBotException {

		AllianzBotSolrSearchDocumentResponse allianzBotSolrServiceResponse = new AllianzBotSolrSearchDocumentResponse();
		Set<AllianzBotSentence> result = new TreeSet<>(new AllianzBotScoreAndHitsComparator());
		// tokenize to query
		List<String> tokensOfTheQuery = Stream.of(allianzBotOpenNlpService.tokenize(keywords)).map(s -> s.toLowerCase())
				.collect(Collectors.toList());
		for (int x = 0; x < documents.size(); x++) {
			
			final String id = documents.get(x).getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID).toString();
			final List<Double> likes = ((List<Double>) documents.get(x)
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES));
			final Double score = new Double(
					documents.get(x).getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_SCORE).toString());

			// log.info("-------------QUESTION ANSWER------------------");
			final List<String> listAnswers = ((List<String>) documents.get(x)
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER));
			final List<String> listQuestions = ((List<String>) documents.get(x)
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION));
			if (CollectionUtils.isNotEmpty(listAnswers) && CollectionUtils.isNotEmpty(listQuestions)
					&& CollectionUtils.isNotEmpty(likes)) {
				AllianzBotSentence mapToAllianzBotSentence = mapToAllianzBotSentence(id, listQuestions.get(0),
						listAnswers.get(0), score, likes.get(0));
				if (null != mapToAllianzBotSentence)
					result.add(mapToAllianzBotSentence);
			}

			// log.info("-------------PLAIN CONTENT------------------");
			@SuppressWarnings("unchecked")
			List<String> contents = (List<String>) documents.get(x).get(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT);
			if (CollectionUtils.isNotEmpty(contents)) {
				String content = contents.get(0);
				try {
					List<AllianzBotSentence> sentences = allianzBotOpenNlpService.sentenceDetect(content);
					for (AllianzBotSentence allianzBotSentence : sentences) {
						if (null != allianzBotSentence) {
							String[] tokensOfTheSentences = allianzBotOpenNlpService
									.tokenize(FileUtils.removeStopWords(allianzBotSentence.getAnswer()));

							// calculating the features
							final double features = Stream.of(tokensOfTheSentences)
									.filter(token -> tokensOfTheQuery.contains(token.toLowerCase())).count();
							final double calculatedScore = calculateScore(features, tokensOfTheSentences.length);
							final String newId = DigestUtils.md5Hex(keywords.concat(allianzBotSentence.getAnswer()));
							AllianzBotSentence allianzBotNewSentence = mapToAllianzBotSentence(newId, keywords,
									allianzBotSentence.getAnswer(), calculatedScore, allianzBotSentence.getLikes());
							if (null != allianzBotNewSentence)
								result.add(allianzBotNewSentence);
						}
					}
				} catch (IOException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				} catch (AllianzBotException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				}
			}
			
			// log.info("-------------TEST DATA------------------");
			@SuppressWarnings("unchecked")
			List<String> failureCategories = (List<String>) documents.get(x).get(AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY);
			if (CollectionUtils.isNotEmpty(failureCategories)) {
				AllianzBotSentence allianzBotNewSentence = mapToAllianzBotSentence(id, keywords,
						failureCategories.get(0), score, likes.get(0));
				if (null != allianzBotNewSentence)
					result.add(allianzBotNewSentence);
			}

		}

		// remove all the duplicate answers
		if (isSearch) {
			Set<AllianzBotSentence> newAbs = new TreeSet<AllianzBotSentence>(new AllianzBotAnswerComparator());
			newAbs.addAll(result);
			result.clear();

			log.info("Second: {}", newAbs);
			// sort all the answers by the high and score
			result.addAll(newAbs);
			newAbs.clear();
		}

		allianzBotSolrServiceResponse.setDocuments(result);
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
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID, document.getId());
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES, document.getLikes());
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION, document.getQuestion());
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER, document.getAnswer());
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