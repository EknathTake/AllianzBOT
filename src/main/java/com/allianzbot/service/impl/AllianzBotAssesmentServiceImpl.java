package com.allianzbot.service.impl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.stereotype.Service;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotAssesmentAnswer;
import com.allianzbot.model.AllianzBotAssesmentObjectives;
import com.allianzbot.model.AllianzBotAssesmentQuestion;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.service.interfaces.IAllianzBotAssesmentService;
import com.allianzbot.utils.AllianzBotConstants;

@Service("allianzBotAssesmentService")
public class AllianzBotAssesmentServiceImpl implements IAllianzBotAssesmentService {

	private Logger log = LoggerFactory.getLogger(AllianzBotAssesmentServiceImpl.class);

	@Value("${url.solr.assesmentCollection}")
	private String assesmentCollection;

	private SolrClient client;

	@Override
	public UpdateResponse storeDocument(AllianzBotDocument allianzBotServiceResponse)
			throws AllianzBotException, SolrServerException, IOException {
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

				if (CollectionUtils.isNotEmpty(columns) && i > 0) {
					if (columns.size() == 3) {
						/** for asssment answers */
						doc = new SolrInputDocument();
						doc.addField(AllianzBotConstants.ABAssesmentAnswers.AB_ANSWER_ID, new Long(columns.get(0)));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID, new Long(columns.get(1)));
						doc.addField(AllianzBotConstants.ABAssesmentAnswers.AB_CORRECT_ANSWER, columns.get(2)
								.replaceAll(AllianzBotConstants.AB_ASSESMENT_SPLITERATOR, AllianzBotConstants.AB_COMMA));
					} else if (columns.size() == 5) {
						/** for asssment questions */
						doc = new SolrInputDocument();
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID, new Long(columns.get(0)));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION, columns.get(1));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_OBJECTIVES, StringUtils.split(columns.get(2), "#$"));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_IS_MULTIANSWERS,
								new Boolean(columns.get(3)));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_TOPIC, columns.get(4));
					}

					client.add(doc);
					if (i % 100 == 0)
						client.commit(); // periodically flush
				}
				i++;
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

	@PostConstruct
	public void initSolr() {
		log.info("SOLR server is starting.");
		client = new HttpSolrClient.Builder(assesmentCollection).build();
		log.info("SOLR server is started.");
	}

	@PreDestroy
	public void destroySolr() throws IOException {

		client.close();
		log.info("SOLR server is stopped.");
	}

	@Override
	public List<AllianzBotAssesmentQuestion> loadAssesmentQuestionsFromSolr(String[] topics)
			throws SolrServerException, IOException {
		String solrAssesmentQuery = prepareForSolrAssesmentQuery(topics);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(solrAssesmentQuery).setStart(0).setRows(AllianzBotConstants.AB_MAX_ROWS).setFields(
				AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID,
				AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION,
				AllianzBotConstants.ABAssesmentQuestion.AB_OBJECTIVES,
				AllianzBotConstants.ABAssesmentQuestion.AB_IS_MULTIANSWERS,
				AllianzBotConstants.ABAssesmentQuestion.AB_TOPIC);

		log.info("Solr Query:{}", solrQuery);
		SolrDocumentList assesmentQuestions = client.query(solrQuery).getResults();
		return assesmentQuestions.stream().map(assesmentQuestion -> {

			AllianzBotAssesmentQuestion allianzBotAssesmentQuestion = new AllianzBotAssesmentQuestion();
			final long questionId = new Long(
					assesmentQuestion.getFieldValue(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID).toString());
			allianzBotAssesmentQuestion.setQuestionId(questionId);

			final String question = assesmentQuestion
					.getFieldValue(AllianzBotConstants.ABKnowledgeSharing.AB_SOLR_FIELD_QUESTION).toString();
			allianzBotAssesmentQuestion.setQuestion(question);

			final Object[] objectives = assesmentQuestion
					.getFieldValues(AllianzBotConstants.ABAssesmentQuestion.AB_OBJECTIVES).toArray();
			// String[] stringArray = Arrays.copyOf(objectives, objectives.length, String[].class);
			 
			AllianzBotAssesmentObjectives[] array = new AllianzBotAssesmentObjectives[objectives.length];
			for(int i=0; i<objectives.length; i++) {
				AllianzBotAssesmentObjectives obj = new AllianzBotAssesmentObjectives();
				obj.setObjective(objectives[i].toString());
				array[i] = obj;
			}
			allianzBotAssesmentQuestion.setObjectives(array );

			final boolean isMultiAnswer = new Boolean(assesmentQuestion
					.getFieldValue(AllianzBotConstants.ABAssesmentQuestion.AB_IS_MULTIANSWERS).toString());
			allianzBotAssesmentQuestion.setIsMultiAnswer(isMultiAnswer);

			final String topic = assesmentQuestion.getFieldValue(AllianzBotConstants.ABAssesmentQuestion.AB_TOPIC)
					.toString();
			allianzBotAssesmentQuestion.setTopic(topic);

			return allianzBotAssesmentQuestion;

		}).collect(Collectors.toList());

	}

	private String prepareForSolrAssesmentQuery(String[] topics) {
		StringBuilder solrAssesmentQuery = new StringBuilder();
		if (ArrayUtils.isNotEmpty(topics)) {
			int i = 0;
			for (String topic : topics) {
				if (i == 0) {
					solrAssesmentQuery.append(AllianzBotConstants.ABAssesmentQuestion.AB_TOPIC)
							.append(AllianzBotConstants.AB_COLON).append(topic);
					i++;
				} else {
					solrAssesmentQuery.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
							.append(AllianzBotConstants.AB_SPACE)
							.append(AllianzBotConstants.ABAssesmentQuestion.AB_TOPIC)
							.append(AllianzBotConstants.AB_COLON).append(topic);
				}
			}
		}

		return solrAssesmentQuery.toString();
	}

	@Override
	public List<AllianzBotAssesmentAnswer> loadAssesmentAnswers(List<Long> questionIds) throws SolrServerException, IOException {
		String answerQueryBuilder = prepareAnswerQuery(questionIds);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(answerQueryBuilder).setStart(0).setRows(AllianzBotConstants.AB_MAX_ROWS).setFields(
				AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID,
				AllianzBotConstants.ABAssesmentAnswers.AB_ANSWER_ID,
				AllianzBotConstants.ABAssesmentAnswers.AB_CORRECT_ANSWER);

		log.info("Inside AllianzBotAssesmentServiceImpl.loadAssesmentAnswers Solr Query :{}", solrQuery);
		SolrDocumentList assesmentQuestions = client.query(solrQuery).getResults();
		
		return assesmentQuestions.stream()
								.map(actualAnswer -> {
									AllianzBotAssesmentAnswer answer = null;
									final Object answerId = actualAnswer.getFieldValue(AllianzBotConstants.ABAssesmentAnswers.AB_ANSWER_ID);
									final Object correstAnswer = actualAnswer.getFieldValue(AllianzBotConstants.ABAssesmentAnswers.AB_CORRECT_ANSWER);
									final Object questionId = actualAnswer.getFieldValue(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID);
									if(null != correstAnswer && null != questionId && null != answerId) {
										answer = new AllianzBotAssesmentAnswer();
										answer.setAnswerId(Long.parseLong(answerId.toString()));
										answer.setQuestionId(Long.parseLong(questionId.toString()));
										answer.setActualAnswer(correstAnswer.toString());
									}
									return answer;
								}).collect(Collectors.toList());
	}

	private String prepareAnswerQuery(List<Long> questionIds) {
		StringBuilder answerQueryBuilder = new StringBuilder();
		if (CollectionUtils.isNotEmpty(questionIds)) {
			int i = 0;
			for (Long questionId : questionIds) {
				if (i == 0) {
					answerQueryBuilder.append(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID)
							.append(AllianzBotConstants.AB_COLON).append(questionId);
					i++;
				} else {
					answerQueryBuilder.append(AllianzBotConstants.AB_SPACE).append(AllianzBotConstants.AB_OR)
							.append(AllianzBotConstants.AB_SPACE)
							.append(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID)
							.append(AllianzBotConstants.AB_COLON).append(questionId);
				}
			}
		}
		return answerQueryBuilder.toString();
	}

}
