package com.allianzbot.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotAssesmentQuestion;
import com.allianzbot.model.AllianzBotAssesmentQuestions;
import com.allianzbot.model.AllianzBotResponseStatus;
import com.allianzbot.process.interfaces.IAllianzBotAssesmentProcess;
import com.allianzbot.process.interfaces.IAllianzBotProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.utils.FileUtils;

/**
 * AssesmentController for the Allianz Bot
 * 
 * @author eknath.take
 *
 */
@RestController
@CrossOrigin(origins = { "*" })
@SessionAttributes("assesmentQuestions")
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

	@GetMapping(value = "/assesment/questions")
	public @ResponseBody List<AllianzBotAssesmentQuestion> loadAssessment(@RequestParam(name = "topic") String topic) {
		log.info("Inside AllianzBotAssesmentController.loadAssessment() topic is {}", topic);

		AllianzBotAssesmentQuestion q1 = new AllianzBotAssesmentQuestion();
		q1.setQuestionId(1);
		q1.setQuestion("Question 1?");
		q1.setObjectives(new String[] { "ob1", "ob2", "ob3", "ob4" });
		q1.setIsMultiAnswer(false);

		AllianzBotAssesmentQuestion q2 = new AllianzBotAssesmentQuestion();
		q2.setQuestionId(2);
		q2.setQuestion("Question 2?");
		q2.setObjectives(new String[] { "ob1", "ob2", "ob3", "ob4" });
		q2.setIsMultiAnswer(true);

		AllianzBotAssesmentQuestion q3 = new AllianzBotAssesmentQuestion();
		q3.setQuestionId(3);
		q3.setQuestion("Question 3?");
		q3.setObjectives(new String[] { "ob1", "ob2", "ob3", "ob4" });
		q3.setIsMultiAnswer(false);

		List<AllianzBotAssesmentQuestion> questions = Arrays.asList(q1, q2, q3);
		// AllianzBotQuestions allianzBotQuestions = new AllianzBotQuestions();
		// allianzBotQuestions.setAllianzBotQuestions(questions);
		return questions;
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
	@GetMapping(value = "/assesment/question")
	public @ResponseBody AllianzBotSolrCreateDocumentResponse saveAssesment() throws FileNotFoundException, IOException,
			SAXException, TikaException, AllianzBotException, SolrServerException {
		log.info("Inside AllianzBotAssesmentController.saveQuestions()");
		MultipartFile[] multipartFiles = fileUtils.storeDocumentFromDir(documents);
		if ( multipartFiles.length > 0) {
			for (MultipartFile multipartFile : multipartFiles) {
				allianzBotAssesmentProcess.storeAssesment(multipartFile);
			}
		}else
			throw new AllianzBotException(000, "Loading Assesment failed.");
		
		AllianzBotSolrCreateDocumentResponse response = new AllianzBotSolrCreateDocumentResponse();
		AllianzBotResponseStatus status = new AllianzBotResponseStatus();
		status.setStatusCode(200);
		status.setMessage("Assesment Stored successfully");
		status.setTimestamp(new Date());
		response.setStatus(status );
		return response;

	}

	/**
	 * Store the answer into the session scope
	 * 
	 * @param allianzBotQuestion
	 */
	@PostMapping(value = "/assesment/answer")
	public void storeUserChoiceAnswers(@RequestBody AllianzBotAssesmentQuestion allianzBotQuestion, HttpSession session) {
		log.info("Inside AllianzBotAssesmentController.saveQuestionsAnswer");
		AllianzBotAssesmentQuestions assesmentQuestions = (AllianzBotAssesmentQuestions) session.getAttribute("assesmentQuestions");
		log.info("Inside AllianzBotAssesmentController.saveQuestionsAnswer before update AllianzBotQuestions :{}",
				assesmentQuestions);
		if (null != assesmentQuestions) {
			Map<Long, AllianzBotAssesmentQuestion> questions = assesmentQuestions.getAllianzBotQuestions();
			questions.put(allianzBotQuestion.getQuestionId(), allianzBotQuestion);
			assesmentQuestions.setAllianzBotQuestions(questions);
			log.info("Inside AllianzBotAssesmentController.saveQuestionsAnswer after update AllianzBotQuestions :{}",
					assesmentQuestions);
			session.setAttribute("assesmentQuestions", assesmentQuestions);
		}
	}
}
