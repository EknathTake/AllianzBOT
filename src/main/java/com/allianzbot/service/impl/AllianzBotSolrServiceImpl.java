package com.allianzbot.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.exception.AllianzBotRunTimeException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.model.AllianzBotSearchResponse;
import com.allianzbot.model.AllianzBotTestCenterData;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;
import com.allianzbot.service.interfaces.IAllianzBotOpenNlpService;
import com.allianzbot.service.interfaces.IAllianzBotSolrService;
import com.allianzbot.solrclient.initializer.SolrClientInitializer;
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

	@Inject
	@Named("allianzBotOpenNlpService")
	private IAllianzBotOpenNlpService allianzBotOpenNlpService;
	
	@Inject
	@Named("solrInitializer")
	private SolrClientInitializer solrClientInitializer;

	private Logger log = LoggerFactory.getLogger(AllianzBotSolrServiceImpl.class);

	@Override
	public UpdateResponse storeDocument(AllianzBotDocument allianzBotServiceResponse)
			throws SolrServerException, IOException {

		SolrInputDocument doc = null;
		Object content = allianzBotServiceResponse.getContent();
		if (content instanceof LinkedHashMap) {
			int i = 0;
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer, List<String>> document = (LinkedHashMap<Integer, List<String>>) content;
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

					solrClientInitializer.client.add(doc);
					if (i % 100 == 0)
						solrClientInitializer.client.commit(); // periodically flush
					i++;
				}
			}

		} else {
			// Indexing Solr Document
			doc = new SolrInputDocument();
			doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES, new Double(0.0));
			doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID, DigestUtils.md5Hex((String) content));
			doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT, (String) content);
			solrClientInitializer.client.add(doc);
		}
		return solrClientInitializer.client.commit();
	}

	@Override
	public AllianzBotSolrSearchDocumentResponse searchDocuments(String query, boolean isSearch)
			throws SolrServerException, IOException, AllianzBotException {

		// prepare for query and keywords
		final String solrSearchQuery = buildQueryAndKeywords(query);

		// Preparing to Solr query
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(solrSearchQuery).setStart(0)
				.setRows(AllianzBotConstants.AB_MAX_ROWS)
				.setFields(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID,
					AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT,
					AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION,
					AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER,
					AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_SCORE,
					AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES,
					AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY,
					AllianzBotConstants.ABTestCenter.AB_FAILED_LOG,
					AllianzBotConstants.ABTestCenter.AB_TEAM,
					AllianzBotConstants.ABTestCenter.AB_TEST_CASE_ID,
					AllianzBotConstants.ABTestCenter.AB_EXECUTION_DATE,
					AllianzBotConstants.ABTestCenter.AB_FAILED_RUN_COUNT,
					AllianzBotConstants.ABTestCenter.AB_DEFECT_ID_STR);

		log.info("Solr Query:{}", solrQuery);
		SolrDocumentList documents = solrClientInitializer.client.query(solrQuery).getResults();

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
			queryBuilder.append(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT)
					.append(AllianzBotConstants.AB_COLON).append(query)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
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
		Set<AllianzBotSearchResponse> result = new TreeSet<>(new AllianzBotScoreAndHitsComparator());
		List<AllianzBotSearchResponse> finalResult = new ArrayList<>();
		// tokenize to query
		List<String> tokensOfTheQuery = Stream
					.of(allianzBotOpenNlpService
					.tokenize(keywords))
					.map(s -> s.toLowerCase())
					.collect(Collectors.toList());
	
		for (int x = 0; x < documents.size(); x++) {
			SolrDocument solrDocument = documents
					.get(x);
			final String id = solrDocument
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID)
					.toString();
			final List<Double> likes = ((List<Double>) solrDocument
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES));
			final Double score = new Double(solrDocument
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_SCORE)
					.toString());

			// log.info("-------------QUESTION ANSWER------------------");
			final List<String> listAnswers = ((List<String>) solrDocument
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER));
			final List<String> listQuestions = ((List<String>) solrDocument
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION));
			
			if (CollectionUtils.isNotEmpty(listAnswers) 
					&& CollectionUtils.isNotEmpty(listQuestions)
					&& CollectionUtils.isNotEmpty(likes)) {
				AllianzBotSearchResponse mapToAllianzBotSentence = new AllianzBotSearchResponse
						.AllianzBotSearchResponseBuilder()
						.setId(id)
						.setQuestion(listQuestions.get(0))
						.setAnswer(listAnswers.get(0))
						.setScore(score)
						.setLikes(likes.get(0))
						.build();
				if (null != mapToAllianzBotSentence)
					result.add(mapToAllianzBotSentence);
			}

			// log.info("-------------PLAIN CONTENT------------------");
			@SuppressWarnings("unchecked")
			List<String> contents = (List<String>) solrDocument
						.get(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT);
			if (CollectionUtils.isNotEmpty(contents)) {
				String content = contents.get(0);
				try {
					List<AllianzBotSearchResponse> sentences = allianzBotOpenNlpService.sentenceDetect(content);
					for (AllianzBotSearchResponse allianzBotSentence : sentences) {
						if (null != allianzBotSentence) {
							String[] tokensOfTheSentences = allianzBotOpenNlpService
									.tokenize(FileUtils.removeStopWords(allianzBotSentence.getAnswer()));

							// calculating the features
							final double features = Stream
									.of(tokensOfTheSentences)
									.filter(token -> tokensOfTheQuery.contains(token.toLowerCase()))
									.count();
							final double calculatedScore = calculateScore(features, tokensOfTheSentences.length);
							final String newId = DigestUtils.md5Hex(keywords.concat(allianzBotSentence.getAnswer()));
							AllianzBotSearchResponse allianzBotNewSentence = new AllianzBotSearchResponse
									.AllianzBotSearchResponseBuilder()
									.setId(newId)
									.setAnswer(allianzBotSentence.getAnswer())
									.setQuestion(keywords)
									.setScore(calculatedScore)
									.setLikes(allianzBotSentence.getLikes())
									.build();
									
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
			
			AllianzBotSearchResponse allianzBotNewSentence = new AllianzBotSearchResponse
					.AllianzBotSearchResponseBuilder()
					.setId(id)
					.setQuestion(keywords)
					.setScore(score)
					.setLikes(likes.get(0))
					.build();
			AllianzBotTestCenterData allianzBotTestCenterData = new AllianzBotTestCenterData();
			@SuppressWarnings("unchecked")
			List<String> failureCategories = (List<String>) solrDocument
					.get(AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY);
			if (CollectionUtils.isNotEmpty(failureCategories)) {
				allianzBotTestCenterData.setFailureCategory(failureCategories.get(0));
			}
			
			@SuppressWarnings("unchecked")
			List<String> failureLog = (List<String>) solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILED_LOG);
			if (CollectionUtils.isNotEmpty(failureLog)) {
				allianzBotTestCenterData.setFailedLog(failureLog.get(0));
			}
			
			@SuppressWarnings("unchecked")
			List<String> teams = (List<String>) solrDocument.get(AllianzBotConstants.ABTestCenter.AB_TEAM);
			if (CollectionUtils.isNotEmpty(teams)) {
				allianzBotTestCenterData.setTeam(teams.get(0));
			}
			
			@SuppressWarnings("unchecked")
			List<String> testCaseId = (List<String>) solrDocument.get(AllianzBotConstants.ABTestCenter.AB_TEST_CASE_ID);
			if (CollectionUtils.isNotEmpty(testCaseId)) {
				allianzBotTestCenterData.setTestCaseId(testCaseId.get(0));
			}
			
			@SuppressWarnings("unchecked")
			List<String> executionDate = (List<String>) solrDocument.get(AllianzBotConstants.ABTestCenter.AB_EXECUTION_DATE);
			if (CollectionUtils.isNotEmpty(executionDate)) {
				allianzBotTestCenterData.setExecutionDate(executionDate.get(0));
			}
			
			@SuppressWarnings("unchecked")
			List<String> failedRunCount = (List<String>) solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILED_RUN_COUNT);
			if (CollectionUtils.isNotEmpty(failedRunCount)) {
				allianzBotTestCenterData.setExecutionDate(failedRunCount.get(0));
			}
			
			@SuppressWarnings("unchecked")
			List<String> defectId = (List<String>) solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILED_RUN_COUNT);
			if (CollectionUtils.isNotEmpty(defectId)) {
				allianzBotTestCenterData.setDefectIdStr(defectId.get(0));
			}
			
			allianzBotNewSentence = new AllianzBotSearchResponse
					.AllianzBotSearchResponseBuilder(allianzBotNewSentence)
					.setAllianzBotTestCenterData(allianzBotTestCenterData)
					.build();
			if (null != allianzBotNewSentence && null != allianzBotNewSentence.getAllianzBotTestCenterData() 
					&& (StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getTeam())
					&& StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getTestCaseId())
					&& StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getExecutionDate())
					&& StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getFailedRunCount())
					&& StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getDefectIdStr())
					&& StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getFailureCategory())
					&& StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getFailedLog())))
				finalResult.add(allianzBotNewSentence);
		}

		// remove all the duplicate answers
		if (isSearch) {
			Set<AllianzBotSearchResponse> newAbs = new TreeSet<AllianzBotSearchResponse>(new AllianzBotAnswerComparator());
			newAbs.addAll(result);
			result.clear();

			log.info("Second: {}", newAbs);
			// sort all the answers by the high and score
			result.addAll(newAbs);
			newAbs.clear();
			result.clear();
		}

		finalResult.addAll(result);
		allianzBotSolrServiceResponse.setDocuments(finalResult);
		return allianzBotSolrServiceResponse;
	}

	private double calculateScore(double features, int totalFeatures) {
		return features / totalFeatures;
	}

	@Override
	public void updateScore(AllianzBotSearchResponse document) throws SolrServerException, IOException, AllianzBotException {

		SolrInputDocument solrInputDocument = new SolrInputDocument();
		if (null != document) {
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID, document.getId());
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES, document.getLikes());
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION, document.getQuestion());
			solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER, document.getAnswer());
		}
		solrClientInitializer.client.add(solrInputDocument);
		solrClientInitializer.client.commit();

	}

	
}

/**
 * Comparator to compare AllianzBotSentence by answer. This will removes the
 * duplicate answers as well.
 * 
 * @author eknath.take
 *
 */
class AllianzBotAnswerComparator implements Comparator<AllianzBotSearchResponse> {

	@Override
	public int compare(AllianzBotSearchResponse abs1, AllianzBotSearchResponse abs2) {
 		if(null == abs1.getAnswer() || null == abs2.getAnswer())
			return 0;
		else
			return abs1.getAnswer().compareTo(abs2.getAnswer());
	}
}

/**
 * Comparator to compare AllianzBotSentence by answer+score of the document.\
 * 
 * @author eknath.take
 *
 */
class AllianzBotScoreAndHitsComparator implements Comparator<AllianzBotSearchResponse> {

	@Override
	public int compare(AllianzBotSearchResponse abs1, AllianzBotSearchResponse abs2) {
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