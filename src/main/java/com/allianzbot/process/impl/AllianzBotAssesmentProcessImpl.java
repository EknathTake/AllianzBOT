package com.allianzbot.process.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

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
import com.allianzbot.model.AllianzBotResponseStatus;
import com.allianzbot.process.interfaces.IAllianzBotAssesmentProcess;
import com.allianzbot.process.interfaces.IAllianzBotProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.service.interfaces.IAllianzBotAssesmentService;

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
		return allianzBotAssesmentService.loadAssesmentQuestionsFromSolr(topics);
	}

	@Override
	public double getAssesmentScore(Map<Long, AllianzBotAssesmentQuestion> assesmentMap) throws SolrServerException, IOException {
		List<Long> ll = assesmentMap.values()
				.stream()
				.map(AllianzBotAssesmentQuestion::getQuestionId)
				.collect(Collectors.toList());
		List<AllianzBotAssesmentAnswer> assesmentAnswers = allianzBotAssesmentService.loadAssesmentAnswers(ll);
		double score = 0;
		for (AllianzBotAssesmentAnswer allianzBotAssesmentAnswer : assesmentAnswers) {
			if(null != allianzBotAssesmentAnswer) {
				/***** user question *****/
				AllianzBotAssesmentQuestion question = assesmentMap.get(allianzBotAssesmentAnswer.getQuestionId());
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
		return score;
	}

}
