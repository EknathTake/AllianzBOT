package com.allianzbot.process.interfaces;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotAssesmentAnswer;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;

public interface IAllianzBotAssesmentProcess {

	/**
	 * Prepare to store all the assesment questions
	 * 
	 * @param multipartFile
	 * @return
	 * @throws AllianzBotException
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	AllianzBotSolrCreateDocumentResponse storeAssesment(MultipartFile multipartFile)
			throws AllianzBotException, SolrServerException, IOException, SAXException, TikaException;

	/**
	 * Get the Answer by questionId
	 * 
	 * @param questionId
	 * @return AllianzBotAssesmentAnswer
	 */
	AllianzBotAssesmentAnswer getAnswerByQuestionId(long questionId);

}
