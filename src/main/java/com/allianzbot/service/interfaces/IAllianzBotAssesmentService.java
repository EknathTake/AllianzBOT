package com.allianzbot.service.interfaces;

import java.io.IOException;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotAssesmentAnswer;
import com.allianzbot.model.AllianzBotAssesmentQuestion;
import com.allianzbot.model.AllianzBotDocument;

public interface IAllianzBotAssesmentService {

	/**
	 * load all the question by topics
	 * @return assesment
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	List<AllianzBotAssesmentQuestion> loadAssesmentQuestionsFromSolr(String []topics) throws SolrServerException, IOException;

	UpdateResponse storeDocument(AllianzBotDocument allianzBotServiceResponse) throws AllianzBotException, SolrServerException, IOException;
	
	List<AllianzBotAssesmentAnswer> loadAssesmentAnswers(List<Long> questionIds) throws SolrServerException, IOException;
}
