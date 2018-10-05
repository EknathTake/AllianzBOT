package com.allianzbot.process.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotAssesmentAnswer;
import com.allianzbot.model.AllianzBotAssesmentObjectives;
import com.allianzbot.model.AllianzBotAssesmentQuestion;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.model.AllianzBotExam;
import com.allianzbot.model.AllianzBotResponseStatus;
import com.allianzbot.model.User;
import com.allianzbot.process.interfaces.IAllianzBotAssesmentProcess;
import com.allianzbot.process.interfaces.IAllianzBotProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.service.interfaces.IAllianzBotAssesmentService;
import com.allianzbot.utils.AllianzBotConstants;

@Component("allianzBotAssesmentProcess")
public class AllianzBotAssesmentProcessImpl implements IAllianzBotAssesmentProcess {

	private Logger log = LoggerFactory.getLogger(AllianzBotAssesmentProcessImpl.class);

	@Inject
	@Named("botTextExtractorProcess")
	private IAllianzBotProcess allianzBotProcess;

	@Inject
	@Named("allianzBotAssesmentService")
	private IAllianzBotAssesmentService allianzBotAssesmentService;

	@Override
	public AllianzBotSolrCreateDocumentResponse storeAssesment(MultipartFile multipartFile)
			throws AllianzBotException, SolrServerException, IOException, SAXException, TikaException {

		// extract content from file
		AllianzBotDocument allianzBotServiceResponse = allianzBotProcess.extractContent(multipartFile);

		// indexing document
		UpdateResponse updateResponse = allianzBotAssesmentService.storeDocument(allianzBotServiceResponse);

		return mapServiceToProcessResponse(allianzBotServiceResponse, updateResponse);
	}

	/**
	 * Map response from service to process layer.
	 * 
	 * @param allianzBotServiceResponse
	 * @param updateResponse
	 * @return {@link AllianzBotSolrCreateDocumentResponse}
	 * @throws IOException
	 */
	private AllianzBotSolrCreateDocumentResponse mapServiceToProcessResponse(
			AllianzBotDocument allianzBotServiceResponse, UpdateResponse updateResponse) throws IOException {

		AllianzBotSolrCreateDocumentResponse allianzBotProcessResponse = new AllianzBotSolrCreateDocumentResponse();
		if (null != allianzBotServiceResponse) {
			AllianzBotDocument allianzBotDocument = new AllianzBotDocument();
			allianzBotDocument.setId(allianzBotServiceResponse.getId());
			allianzBotDocument.setContent(allianzBotServiceResponse.getContent());
			allianzBotProcessResponse.setAllianzBotDocument(allianzBotDocument);
		}

		if (null != updateResponse) {
			AllianzBotResponseStatus status = new AllianzBotResponseStatus();
			status.setMessage(updateResponse.toString());
			status.setStatusCode(updateResponse.getStatus());
			allianzBotProcessResponse.setStatus(status);
		}
		return allianzBotProcessResponse;
	}

	@Override
	public List<AllianzBotAssesmentQuestion> loadAssesmentQuestions(String[] topics) throws SolrServerException, IOException {
		List<AllianzBotAssesmentQuestion> loadAssesmentQuestionsFromSolr = allianzBotAssesmentService.loadAssesmentQuestionsFromSolr(topics);
		//load any 10 random questions from list
		Collections.shuffle(loadAssesmentQuestionsFromSolr);
		return loadAssesmentQuestionsFromSolr
					.stream()
					.limit(10)
					.collect(Collectors.toList());
	}

	@Override
	public AllianzBotExam getAssesmentScore(Map<String, AllianzBotAssesmentQuestion> assesmentMap, String userId) throws SolrServerException, IOException {
		List<Long> questionIds = new ArrayList<>();
		for(String currentKey : assesmentMap.keySet()){
			String[] keyArr = StringUtils.split(currentKey, AllianzBotConstants.AB_ASSESMENT_SPLITERATOR);
			String userID = keyArr[0];
			if(userId.equals(userID)) {
				Long questionId = Long.parseLong(keyArr[1]);
				questionIds.add(questionId);
				
			}
		}
		
		String topic = StringUtils.EMPTY;
		List<AllianzBotAssesmentAnswer> assesmentAnswers = allianzBotAssesmentService.loadAssesmentAnswers(questionIds);
		double score = 0;
		for (AllianzBotAssesmentAnswer allianzBotAssesmentAnswer : assesmentAnswers) {
			if(null != allianzBotAssesmentAnswer) {
				/***** user question *****/
				AllianzBotAssesmentQuestion question = assesmentMap.get(userId+AllianzBotConstants.AB_ASSESMENT_SPLITERATOR+allianzBotAssesmentAnswer.getQuestionId());
				if(null != question) {
					topic = question.getTopic();
					AllianzBotAssesmentObjectives[] userObjectiveAnswers = question.getObjectives();
					String userAnswer = Stream.of(userObjectiveAnswers)
											.filter(Objects::nonNull)
											.filter(AllianzBotAssesmentObjectives::isChecked)
											.map(mapper -> mapper.getObjective())
											.collect(Collectors.joining(","));	
					for (AllianzBotAssesmentAnswer assesmentAnswer : assesmentAnswers) {
						if(null != assesmentAnswer) {
							if(userAnswer.equals(assesmentAnswer.getActualAnswer())
									&& allianzBotAssesmentAnswer.getQuestionId() == assesmentAnswer.getQuestionId()) {
								score +=5;
							}
						}
					}
				}
			}
		}
		
		AllianzBotExam exam = new AllianzBotExam();
		exam.setFinishTime(LocalDateTime.now());
		/**TODO: need to remove hard coded values for userName**/
		User user = new User();
		user.setUserId(userId);
		user.setUsername("Eknath Take");
		exam.setUser(user);
		exam.setScore(score);
		exam.setTopic(topic);
		exam.setTotalMarks(questionIds.size()*5);
		exam.setPercentages((exam.getScore()/exam.getTotalMarks())*100);
		
		/**TODO: Uncomment this to enable sending email.**/
		//allianzBotAssesmentService.sendAssesmentScoreMailToLead(exam);
		return exam;
	}

}
