package com.allianzbot.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
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
		client = new HttpSolrClient.Builder(SOLR_COLLECTION1).build();
		SolrInputDocument doc = null;
		Object content = allianzBotServiceResponse.getContent();
		if (content instanceof LinkedHashMap) {

			// in following HashMap Key is Row number and values are all the Columns
			LinkedHashMap<Integer, List<String>> document = (LinkedHashMap<Integer, List<String>>) content;
			// iterate through all the columns
			int i = 0;
			for (Map.Entry<Integer, List<String>> entry : document.entrySet()) {
				Integer rows = entry.getKey();
				List<String> columns = entry.getValue();
				if (columns.size() == 2 && rows > 0) {
					doc = new SolrInputDocument();
					doc.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_HITS, new Double(0.0));
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
			doc.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_HITS, new Double(0.0));
			doc.addField(AllianzBotConstants.AB_SOLR_FIELD_ID, DigestUtils.md5Hex((String) content));
			doc.addField(AllianzBotConstants.AB_SOLR_FIELD_CONTENT, (String) content);
			client.add(doc);
		}
		return client.commit();
	}

	@Override
	public AllianzBotSolrSearchDocumentResponse searchDocuments(Map<String, String> queryMap)
			throws SolrServerException, IOException, AllianzBotException {

		// prepare for query and keywords
		List<String> builder = buildQueryAndKeywords(queryMap);

		// Preparing to Solr query
		client = new HttpSolrClient.Builder(SOLR_COLLECTION1).build();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(builder.get(1)).setStart(0).setRows(AllianzBotConstants.AB_MAX_ROWS).setHighlight(true)
				.setFields(AllianzBotConstants.AB_SOLR_FIELD_ID, AllianzBotConstants.AB_SOLR_FIELD_CONTENT,
						AllianzBotConstants.AB_SOLR_FIELD_QUESTION, AllianzBotConstants.AB_SOLR_FIELD_ANSWER,
						AllianzBotConstants.SOLR_FIELD_SCORE, AllianzBotConstants.AB_SOLR_FIELD_TOTAL_HITS);

		log.info("Solr Query:{}", solrQuery);
		SolrDocumentList documents = client.query(solrQuery).getResults();

		return mapToAllianzBotSolrSearchDocumentResponse(builder.get(0), documents);
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
	private List<String> buildQueryAndKeywords(Map<String, String> queryMap) throws AllianzBotException {
		StringBuilder queryBuilder = new StringBuilder();
		StringBuilder keywordsBuilder = new StringBuilder();

		if (queryMap.size() > 0) {
			List<String> keys = queryMap.keySet().stream().collect(Collectors.toList());
			List<String> values = queryMap.values().stream().collect(Collectors.toList());
			IntStream.range(0, keys.size()).forEach(index -> {
				if (index == 0) {
					// collect the user query keywords, useful for filtering
					keywordsBuilder.append(values.get(index));

					// bulding the user query
					queryBuilder.append(keys.get(index)).append(AllianzBotConstants.AB_COLON).append(values.get(index));
				} else {
					// bulding the user query
					queryBuilder.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
							.append(AllianzBotConstants.AB_SPACE).append(keys.get(index)).append(AllianzBotConstants.AB_COLON)
							.append(values.get(index));
				}
			});

			List<String> builder = new ArrayList<>();
			builder.add(keywordsBuilder.toString());
			builder.add(queryBuilder.toString());
			return builder;

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
		Set<AllianzBotSentence> result = new TreeSet<>(new AllianzBotScoreAndHitsComparator());
		// tokenize to query
		String[] tokenizedQuery = allianzBotOpenNlpService.tokenize(keywords);
		documents.stream().forEach(x -> {
			@SuppressWarnings("unchecked")
			List<String> listAnswers = (List<String>) x.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_ANSWER);
			@SuppressWarnings("unchecked")
			List<String> listQuestions = (List<String>) x.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_QUESTION);
			final String id = new String(x.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_ID).toString());
			@SuppressWarnings("unchecked")
			final List<Double> hits = (List<Double>) x.getFieldValue(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_HITS);

			// log.info("-------------ANSWER------------------");
			if (CollectionUtils.isNotEmpty(listAnswers) && CollectionUtils.isNotEmpty(listQuestions)
					&& CollectionUtils.isNotEmpty(hits)) {
				if (listAnswers.get(0).split(" ").length > 2)
					result.add(mapToAllianzBotSentence(id, listQuestions.get(0), listAnswers.get(0),
							new Double(x.get(AllianzBotConstants.SOLR_FIELD_SCORE).toString()), hits.get(0)));
			}

			// log.info("-------------CONTENT------------------");
			@SuppressWarnings("unchecked")
			List<String> contents = (List<String>) x.get(AllianzBotConstants.AB_SOLR_FIELD_CONTENT);
			if (CollectionUtils.isNotEmpty(contents) && contents.size() > 0) {
				String content = contents.get(0);
				try {
					// raw sentences
					List<AllianzBotSentence> sentences = allianzBotOpenNlpService.sentenceDetect(content);
					// Iterate through all the sentences
					for (AllianzBotSentence allianzBotSentence : sentences) {
						if (null != allianzBotSentence) {

							// calculating the features
							double features = 0;
							String[] tokenizedSentence = allianzBotOpenNlpService
									.tokenize(FileUtils.removeStopWords(allianzBotSentence.getAnswer()));
							for (int i = 0; i < tokenizedSentence.length; i++) {
								for (int j = 0; j < tokenizedQuery.length; j++) {
									if (tokenizedSentence[i].equalsIgnoreCase(tokenizedQuery[j]))
										features++;
								}
							}

							final double score = calculateScore(features, tokenizedSentence);
							AllianzBotSentence allianzBotNewSentence = mapToAllianzBotSentence(id, keywords,
									allianzBotSentence.getAnswer(), score, allianzBotSentence.getHits());
							if (allianzBotNewSentence.getAnswer().split(" ").length > 2)
								result.add(allianzBotNewSentence);

						}
					}
				} catch (IOException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				} catch (AllianzBotException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				}
			}
		});

		// remove all the duplicate answers
		Set<AllianzBotSentence> newAbs = new TreeSet<AllianzBotSentence>(new AllianzBotScoreAndHitsComparator());
		newAbs.addAll(result);
		//newAbs.sort(new AllianzBotScoreAndHitsComparator());

		result.clear();
		// sort all the answers by the high score
		result.addAll(newAbs);
		newAbs.clear();

		allianzBotSolrServiceResponse.setDocuments(result.stream().collect(Collectors.toList()));
		return allianzBotSolrServiceResponse;
	}

	private AllianzBotSentence mapToAllianzBotSentence(String id, String question, String answer, double score,
			double hits) {
		AllianzBotSentence allianzBotNewSentence = new AllianzBotSentence();
		allianzBotNewSentence.setId(id);
		allianzBotNewSentence.setScore(score);
		allianzBotNewSentence.setQuestion(question);
		allianzBotNewSentence.setAnswer(answer);
		allianzBotNewSentence.setHits(hits);
		return allianzBotNewSentence;

	}

	private double calculateScore(double features, String[] tokenizedSentence) {
		return features / tokenizedSentence.length;
	}

	@Override
	// public UpdateResponse updateScore(String documentId, double hitCount)
	public void updateScore(AllianzBotSentence document) throws SolrServerException, IOException, AllianzBotException {
		
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put(AllianzBotConstants.AB_SOLR_FIELD_ID, document.getId());
		AllianzBotSolrSearchDocumentResponse result = searchDocuments(queryMap);

		client = new HttpSolrClient.Builder(SOLR_COLLECTION1).build();
		SolrInputDocument solrInputDocument = new SolrInputDocument();

		/** Document already exist, only update the score here */
		if (result.getDocuments().size() > 0) {
			AllianzBotSentence allianzBotSentence = result.getDocuments().get(0);
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_ID, allianzBotSentence.getId());
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_HITS,
					Double.sum(allianzBotSentence.getHits(), document.getHits()));
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_QUESTION, allianzBotSentence.getQuestion());
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_ANSWER, allianzBotSentence.getAnswer());
		}
		/** Document not found, Insert new document here. */
		else {
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_ID, DigestUtils.md5Hex(document.getQuestion() + document.getAnswer()));
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_TOTAL_HITS, document.getHits());
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_QUESTION, document.getQuestion());
			solrInputDocument.addField(AllianzBotConstants.AB_SOLR_FIELD_ANSWER, document.getAnswer());
		}
		client.add(solrInputDocument);
		client.commit();

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
		final double thisScore = Double.sum(abs1.getScore(), abs1.getHits());
		final double allianzBotScore = Double.sum(abs2.getScore(), abs2.getHits());
		if (thisScore == allianzBotScore) {
			return 0;
		} else if (thisScore > allianzBotScore) {
			return -1;
		}
		return 1;
	}
}