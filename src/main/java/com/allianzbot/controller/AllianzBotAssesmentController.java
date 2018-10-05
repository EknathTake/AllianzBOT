package com.allianzbot.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotAssesmentQuestion;
import com.allianzbot.model.AllianzBotExam;
import com.allianzbot.model.AllianzBotResponseStatus;
import com.allianzbot.process.interfaces.IAllianzBotAssesmentProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.utils.AllianzBotConstants;
import com.allianzbot.utils.FileUtils;

/**
 * AssesmentController for the Allianz Bot
 * 
 * @author eknath.take
 *
 */
@RestController
@CrossOrigin(origins = { "*" })
public class AllianzBotAssesmentController {

	private Logger log = LoggerFactory.getLogger(AllianzBotAssesmentController.class);

	@Value("${file.path.assesmentDocuments}")
	private String documents;

	@Inject
	@Named("allianzBotAssesmentProcess")
	private IAllianzBotAssesmentProcess allianzBotAssesmentProcess;

	@Inject
	@Named("fileUtils")
	private FileUtils fileUtils;

	@Resource(name = "assesmentMap")
	private Map<String, AllianzBotAssesmentQuestion> assesmentMap;

	@Value("${exam.time.minutes}")
	private Integer examTimeMins;

	/**
	 * Load the Assesment related questions related to topic.
	 * 
	 * @param topic
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@GetMapping(value = "/assesment/questions")
	public @ResponseBody List<AllianzBotAssesmentQuestion> loadAssessmentQuestions(
			@RequestParam(name = "topic") String topic)
			throws SolrServerException, IOException {
		log.info("AllianzBotAssesmentController.loadAssessment() topic is: {}", topic);
		//request.getSession().setAttribute("examStarted", new Date().getTime());
		// final int remaining = getRemainingTime(request);
		// model.addAttribute("examTime", remaining);
		return allianzBotAssesmentProcess.loadAssesmentQuestions(new String[] { topic });
	}

	/**
	 * Store the assesment question into the solr server
	 * 
	 * @param allianzBotQuestion
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SolrServerException
	 * @throws AllianzBotException
	 * @throws TikaException
	 * @throws SAXException
	 */
	@PostMapping(value = "/assesment/questions")
	public @ResponseBody AllianzBotSolrCreateDocumentResponse storeAssesmentIntoSolr()
			throws FileNotFoundException, IOException, SAXException, TikaException, AllianzBotException,
			SolrServerException {

		log.info("AllianzBotAssesmentController.saveAssesment() started{}");
		MultipartFile[] multipartFiles = fileUtils.storeDocumentFromDir(documents);
		if (multipartFiles.length > 0) {
			for (MultipartFile multipartFile : multipartFiles) {
				allianzBotAssesmentProcess.storeAssesment(multipartFile);
			}
		} else
			throw new AllianzBotException(000, "Loading Assesment failed.");

		return prepareAllianzBotSolrCreateDocumentResponse();
	}

	private AllianzBotSolrCreateDocumentResponse prepareAllianzBotSolrCreateDocumentResponse() {
		AllianzBotSolrCreateDocumentResponse response = new AllianzBotSolrCreateDocumentResponse();
		AllianzBotResponseStatus status = new AllianzBotResponseStatus();
		status.setStatusCode(200);
		status.setMessage("Assesment Stored successfully");
		status.setTimestamp(new Date());
		response.setStatus(status);
		return response;

	}

	/**
	 * @param allianzBotQuestion
	 */
	@PostMapping(value = "/assesment/answer")
	public void storeUserChoiceAnswers(@RequestBody AllianzBotAssesmentQuestion allianzBotQuestion, @RequestHeader("User-Id") String userId) {
		log.info("AllianzBotAssesmentController.saveQuestionsAnswer started");
		assesmentMap.put(userId+AllianzBotConstants.AB_ASSESMENT_SPLITERATOR+allianzBotQuestion.getQuestionId(), allianzBotQuestion);
		log.info("AllianzBotAssesmentController.saveQuestionsAnswer Finished");
	}

	@PostMapping(value = "/assesment/finished")
	public @ResponseBody AllianzBotExam exitAssesment(@RequestHeader("User-Id") String userId) throws SolrServerException, IOException {
		log.info("Assesment: {}", assesmentMap);
		// load all the answers
		// check the correct answers
		AllianzBotExam exam = allianzBotAssesmentProcess.getAssesmentScore(assesmentMap, userId);
		assesmentMap.clear();
		return exam;
	}

	/*
	 * @GetMapping(value = "/time")
	 * 
	 * @ResponseBody public Integer timer(HttpServletRequest request) { return
	 * getRemainingTime(request); }
	 * 
	 * private int getRemainingTime(HttpServletRequest request) { final long start =
	 * (long) request.getSession().getAttribute("examStarted"); final int remaining
	 * = (int) ((examTimeMins * 60) - ((Calendar.getInstance().getTimeInMillis() -
	 * start) / 1000)); return remaining; }
	 */
}
