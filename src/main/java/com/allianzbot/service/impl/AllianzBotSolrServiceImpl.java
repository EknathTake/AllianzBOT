package com.allianzbot.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
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
				int j=0;
				List<String> columns = entry.getValue();
				if (CollectionUtils.isNotEmpty(columns)) {
					if (columns.size() == 2) {
						if(j==0) {
							doc = new SolrInputDocument();
							j++;
						}else {
							/**for training related data*/
							doc = new SolrInputDocument();
							doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID,
									DigestUtils.md5Hex(columns.get(0) + columns.get(1)));
							doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION, columns.get(0));
							doc.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER, columns.get(1));
						}

					} else if (columns.size() == 16) {
						if(j==0) {
							doc = new SolrInputDocument();
							j++;
						}else {
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
					AllianzBotConstants.ABTestCenter.AB_DEFECT_ID_STR,
					AllianzBotConstants.ABTestCenter.AB_DEFECT_ID,
					AllianzBotConstants.ABTestCenter.AB_AUTO_STATUS,
					AllianzBotConstants.ABTestCenter.AB_REQUIREMENTS_ID,
					AllianzBotConstants.ABTestCenter.AB_RISK_CLASS,
					AllianzBotConstants.ABTestCenter.AB_TEST_SET_ID,
					AllianzBotConstants.ABTestCenter.AB_TEST_LAB_PATH,
					AllianzBotConstants.ABTestCenter.AB_EXECUTION_STATUS,
					AllianzBotConstants.ABTestCenter.AB_FAILED_STEP,
					AllianzBotConstants.ABTestCenter.AB_SCREEN_SHOT_URL);

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
					.append(AllianzBotConstants.AB_STAR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID)
					.append(AllianzBotConstants.AB_COLON).append(query)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID_STR)
					.append(AllianzBotConstants.AB_COLON).append(query)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
					.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.ABTestCenter.AB_TEST_CASE_ID)
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
	private AllianzBotSolrSearchDocumentResponse prepareForAllianzBotSolrSearchDocumentResponse(final String keywords,
			SolrDocumentList documents, boolean isSearch)
			throws InvalidFormatException, IOException, AllianzBotException {

		AllianzBotSolrSearchDocumentResponse allianzBotSolrServiceResponse = new AllianzBotSolrSearchDocumentResponse();
		List<AllianzBotSearchResponse> resultByScoreAndHits = new ArrayList<>();
		List<AllianzBotSearchResponse> resultByTestCaseId = new ArrayList<>();
		// tokenize to query
		List<String> tokensOfTheQuery = Stream
					.of(allianzBotOpenNlpService
					.tokenize(keywords))
					.map(s -> s.toLowerCase())
					.collect(Collectors.toList());
	
		for (int x = 0; x < documents.size(); x++) {
			SolrDocument solrDocument = documents
					.get(x);
			//log.info("Solr Document :{}", solrDocument);
			final String id = solrDocument
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ID)
					.toString();
			Object likes = solrDocument.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_TOTAL_LIKES);
			Object score = solrDocument.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_SCORE);

			// log.info("-------------QUESTION ANSWER------------------");
			Object listAnswers = solrDocument.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER);
			Object listQuestions = solrDocument.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION);
			
			if (Objects.nonNull(listAnswers) && Objects.nonNull(listQuestions)) {
				AllianzBotSearchResponse mapToAllianzBotSentence = new AllianzBotSearchResponse
						.AllianzBotSearchResponseBuilder()
						.setId(id)
						.setQuestion(listQuestions.toString())
						.setAnswer(listAnswers.toString())
						.setScore(Double.parseDouble((Objects.nonNull(score) ? score.toString() : "0" )))
						.setLikes(Double.parseDouble((Objects.nonNull(likes) ? likes.toString() : "0" )))
						.build();
				if (null != mapToAllianzBotSentence)
					resultByScoreAndHits.add(mapToAllianzBotSentence);
			}

			// log.info("-------------PLAIN CONTENT------------------");
			Object contents = solrDocument.get(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_CONTENT);
			if (Objects.nonNull(contents)) {
				try {
					List<AllianzBotSearchResponse> sentences = allianzBotOpenNlpService.sentenceDetect(contents.toString());
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
								resultByScoreAndHits.add(allianzBotNewSentence);
						}
					}
				} catch (IOException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				} catch (AllianzBotException e) {
					throw new AllianzBotRunTimeException(000, e.getMessage());
				}
			}
			
			// log.info("-------------TEST DATA Center------------------");
			AllianzBotSearchResponse allianzBotNewSentence = new AllianzBotSearchResponse
					.AllianzBotSearchResponseBuilder()
					.setId(id)
					.setQuestion(keywords)
					.setScore(Double.parseDouble((Objects.nonNull(score) ? score.toString() : "0" )))
					.setLikes(Double.parseDouble((Objects.nonNull(likes) ? likes.toString() : "0" )))
					.build();
			AllianzBotTestCenterData allianzBotTestCenterData = mapperSolrDocumentsToAllianzBotTestCenterData(solrDocument);
			allianzBotNewSentence = new AllianzBotSearchResponse
					.AllianzBotSearchResponseBuilder(allianzBotNewSentence)
					.setAllianzBotTestCenterData(allianzBotTestCenterData)
					.build();
			if (null != allianzBotNewSentence && null != allianzBotNewSentence.getAllianzBotTestCenterData() 
					&& (StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getTeam())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getTestCaseId())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getExecutionDate())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getFailedRunCount())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getDefectIdStr())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getFailureCategory())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getFailedLog())
					|| StringUtils.isNotEmpty(allianzBotNewSentence.getAllianzBotTestCenterData().getDefectId())))
				resultByTestCaseId.add(allianzBotNewSentence);
		}

		// remove all the duplicate answers
		if (isSearch) {
			Set<AllianzBotSearchResponse> newAbs = new TreeSet<AllianzBotSearchResponse>(new AllianzBotAnswerComparator());
			newAbs.addAll(resultByScoreAndHits);
			resultByScoreAndHits.clear();
			resultByScoreAndHits.addAll(newAbs);
			newAbs.clear();
			
			//finalResult.addAll(result);
			Map<String, Long> ss = resultByTestCaseId.stream()
					.filter(absr -> null != absr.getAllianzBotTestCenterData())
					.collect(Collectors.groupingBy( 
							(AllianzBotSearchResponse absr) -> absr.getAllianzBotTestCenterData().getFailedLog(),
							Collectors.counting()));
			Supplier<TreeSet<AllianzBotSearchResponse>> supplier = () -> new TreeSet<AllianzBotSearchResponse>(new AllianzBotFailedLogComparator());
			Set<AllianzBotSearchResponse> testCenterData = resultByTestCaseId.stream()
					.filter(absr -> null!= absr.getAllianzBotTestCenterData())
					.filter(absr -> null !=ss.get(absr.getAllianzBotTestCenterData().getFailedLog()))
					.map(absr -> allianzBotSearchResponseMapper(ss, absr))
					.collect(Collectors.toCollection(supplier));
			
			resultByScoreAndHits.addAll(testCenterData);
			//System.out.println("=======Before Sort===============");
			//resultByScoreAndHits.forEach(System.out::println);
			Collections.sort(resultByScoreAndHits, new AllianzBotScoreAndHitsComparator());
			//System.out.println("=========After Sort=============");
			//resultByScoreAndHits.forEach(System.out::println);
			testCenterData.clear();

			allianzBotSolrServiceResponse.setDocuments(resultByScoreAndHits);
		}else {
			//System.out.println("=======Search Result While Updating===============");
			//resultByTestCaseId.forEach(System.out::println);
			allianzBotSolrServiceResponse.setDocuments(resultByTestCaseId);
		}
			
		return allianzBotSolrServiceResponse;
	}

	private AllianzBotTestCenterData mapperSolrDocumentsToAllianzBotTestCenterData(SolrDocument solrDocument) {
		AllianzBotTestCenterData allianzBotTestCenterData = new AllianzBotTestCenterData();
		
		Object failureCategories = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY);
		allianzBotTestCenterData.setFailureCategory((Objects.nonNull(failureCategories)) ? failureCategories.toString() : null);
		
		Object defectId = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID);
		allianzBotTestCenterData.setDefectId((Objects.nonNull(defectId)) ? defectId.toString() : null);
		
		Object failureLog = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILED_LOG);
		allianzBotTestCenterData.setFailedLog((Objects.nonNull(failureLog)) ? failureLog.toString() : null);
		
		Object teams = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_TEAM);
		allianzBotTestCenterData.setTeam((Objects.nonNull(teams)) ? teams.toString() : null);
		
		Object testCaseId = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_TEST_CASE_ID);
		allianzBotTestCenterData.setTestCaseId((Objects.nonNull(testCaseId)) ? testCaseId.toString() : null);
		
		Object executionDate = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_EXECUTION_DATE);
		allianzBotTestCenterData.setExecutionDate((Objects.nonNull(executionDate)) ? executionDate.toString() : null);
		
		Object failedRunCount = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILED_RUN_COUNT);
		allianzBotTestCenterData.setFailedRunCount((Objects.nonNull(failedRunCount)) ? failedRunCount.toString() : null);
		
		Object defectIdStr = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID_STR);
		allianzBotTestCenterData.setDefectIdStr((Objects.nonNull(defectIdStr)) ? defectIdStr.toString() : null);
		
		Object autoStatus = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_AUTO_STATUS);
		allianzBotTestCenterData.setAutoStatus((Objects.nonNull(autoStatus)) ? autoStatus.toString() : null);

		Object requirementsId = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_REQUIREMENTS_ID);
		allianzBotTestCenterData.setRequirementsId((Objects.nonNull(requirementsId)) ? requirementsId.toString() : null);
		
		Object riskClass = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_RISK_CLASS);
		allianzBotTestCenterData.setRiskClass((Objects.nonNull(riskClass)) ? riskClass.toString() : null);
		
		Object testSetId = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_TEST_SET_ID);
		allianzBotTestCenterData.setTestSetId((Objects.nonNull(testSetId)) ? testSetId.toString() : null);
		
		Object testLabPath = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_TEST_LAB_PATH);
		allianzBotTestCenterData.setTestLabPath((Objects.nonNull(testLabPath)) ? testLabPath.toString() : null);
		
		Object executionStatus = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_EXECUTION_STATUS);
		allianzBotTestCenterData.setExecutionStatus((Objects.nonNull(executionStatus)) ? executionStatus.toString() : null);
		
		Object failedStep = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_FAILED_STEP);
		allianzBotTestCenterData.setFailedStep((Objects.nonNull(failedStep)) ? failedStep.toString() : null);
		
		Object screenShotPath = solrDocument.get(AllianzBotConstants.ABTestCenter.AB_SCREEN_SHOT_URL);
		allianzBotTestCenterData.setScreenShotPath((Objects.nonNull(screenShotPath)) ? screenShotPath.toString() : null);
		
		return allianzBotTestCenterData;
	}

	private AllianzBotSearchResponse allianzBotSearchResponseMapper(Map<String, Long> ss,
			AllianzBotSearchResponse absr) {
		AllianzBotSearchResponse newAbsr = null;
		AllianzBotTestCenterData allianzBotTestCenterData2 = absr.getAllianzBotTestCenterData();
		if(null != absr && null != allianzBotTestCenterData2 && ss.size()>0) {
			allianzBotTestCenterData2.setErrorCount(ss.get(allianzBotTestCenterData2.getFailedLog()));
			AllianzBotTestCenterData allianzBotTestCenterData = allianzBotTestCenterDataMapper(allianzBotTestCenterData2);
			newAbsr = new AllianzBotSearchResponse
							.AllianzBotSearchResponseBuilder()
					        .setAllianzBotTestCenterData(allianzBotTestCenterData )
					        .setAnswer(absr.getAnswer())
					        .setId(absr.getId())
					        .setLikes(absr.getLikes())
					        .setQuestion(absr.getQuestion())
					        .setScore(absr.getScore())
					        .build();
		}
		return newAbsr;
	}

	private AllianzBotTestCenterData allianzBotTestCenterDataMapper(AllianzBotTestCenterData allianzBotTestCenterData2) {
		AllianzBotTestCenterData allianzBotTestCenterData = new AllianzBotTestCenterData();
		allianzBotTestCenterData.setAutoStatus(allianzBotTestCenterData2.getAutoStatus());
		allianzBotTestCenterData.setDefectId(allianzBotTestCenterData2.getDefectId());
		allianzBotTestCenterData.setDefectIdStr(allianzBotTestCenterData2.getDefectIdStr());
		allianzBotTestCenterData.setErrorCount(allianzBotTestCenterData2.getErrorCount());
		allianzBotTestCenterData.setExecutionDate(allianzBotTestCenterData2.getExecutionDate());
		allianzBotTestCenterData.setExecutionStatus(allianzBotTestCenterData2.getExecutionStatus());
		allianzBotTestCenterData.setFailedLog(allianzBotTestCenterData2.getFailedLog());
		allianzBotTestCenterData.setFailedRunCount(allianzBotTestCenterData2.getFailedRunCount());
		allianzBotTestCenterData.setFailedStep(allianzBotTestCenterData2.getFailedStep());
		allianzBotTestCenterData.setFailureCategory(allianzBotTestCenterData2.getFailureCategory());
		allianzBotTestCenterData.setRequirementsId(allianzBotTestCenterData2.getRequirementsId());
		allianzBotTestCenterData.setRiskClass(allianzBotTestCenterData2.getRiskClass());
		allianzBotTestCenterData.setScreenShotPath(allianzBotTestCenterData2.getScreenShotPath());
		allianzBotTestCenterData.setTeam(allianzBotTestCenterData2.getTeam());
		allianzBotTestCenterData.setFailedRunCount(allianzBotTestCenterData2.getFailedRunCount());
		allianzBotTestCenterData.setTestCaseId(allianzBotTestCenterData2.getTestCaseId());
		allianzBotTestCenterData.setTestLabPath(allianzBotTestCenterData2.getTestLabPath());
		allianzBotTestCenterData.setTestSetId(allianzBotTestCenterData2.getTestSetId());
		return allianzBotTestCenterData;
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
			if(StringUtils.isNotEmpty(document.getAnswer())) {
				solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION, document.getQuestion());
				solrInputDocument.addField(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_ANSWER, document.getAnswer());
			}else {
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_TEAM, document.getAllianzBotTestCenterData().getTeam());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_TEST_CASE_ID, document.getAllianzBotTestCenterData().getTestCaseId());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_AUTO_STATUS, document.getAllianzBotTestCenterData().getAutoStatus());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_REQUIREMENTS_ID, document.getAllianzBotTestCenterData().getRequirementsId());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_RISK_CLASS, document.getAllianzBotTestCenterData().getRiskClass());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_TEST_SET_ID, document.getAllianzBotTestCenterData().getTestSetId());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_TEST_LAB_PATH, document.getAllianzBotTestCenterData().getTestLabPath());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_EXECUTION_DATE, document.getAllianzBotTestCenterData().getExecutionDate());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_EXECUTION_STATUS, document.getAllianzBotTestCenterData().getExecutionStatus());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_FAILED_RUN_COUNT, document.getAllianzBotTestCenterData().getFailedRunCount());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID_STR, document.getAllianzBotTestCenterData().getDefectIdStr());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_FAILED_STEP, document.getAllianzBotTestCenterData().getFailedStep());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_FAILED_LOG, document.getAllianzBotTestCenterData().getFailedLog());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_SCREEN_SHOT_URL, document.getAllianzBotTestCenterData().getScreenShotPath());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_FAILURE_CATEGORY, document.getAllianzBotTestCenterData().getFailureCategory());
				solrInputDocument.addField(AllianzBotConstants.ABTestCenter.AB_DEFECT_ID, document.getAllianzBotTestCenterData().getDefectId());
			}
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

class AllianzBotTestCaseIdComparator implements Comparator<AllianzBotSearchResponse> {
	@Override
	public int compare(AllianzBotSearchResponse abs1, AllianzBotSearchResponse abs2) {
		if(null == abs1.getAllianzBotTestCenterData() || null == abs2.getAllianzBotTestCenterData())
			return 0;
		else	
			return abs1.getAllianzBotTestCenterData()
					.getTestCaseId()
					.compareTo(abs2
							.getAllianzBotTestCenterData()
							.getTestCaseId());
	}
}

class AllianzBotFailedLogComparator implements Comparator<AllianzBotSearchResponse> {
	@Override
	public int compare(AllianzBotSearchResponse abs1, AllianzBotSearchResponse abs2) {
		if(null == abs1.getAllianzBotTestCenterData() || null == abs2.getAllianzBotTestCenterData())
			return 0;
		else	
			return abs1.getAllianzBotTestCenterData()
					.getFailedLog()
					.compareTo(abs2
							.getAllianzBotTestCenterData()
							.getFailedLog());
	}
}