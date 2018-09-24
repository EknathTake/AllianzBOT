package com.allianzbot.process.interfaces;

import java.io.IOException;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.dto.AllianzBotSentence;
import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;

/**
 * Process layer interface. Contents extracted from documents based on the
 * content/type of the document
 * 
 * @author eknath.take
 *
 */
public interface IAllianzBotProcess {

	/**
	 * Extract the text from uploaded document and store it into the solr server.
	 * 
	 * @param inputStream
	 * @return content
	 */
	AllianzBotSolrCreateDocumentResponse storeDocument(MultipartFile file)
			throws IOException, SAXException, TikaException, AllianzBotException, SolrServerException;

	/**
	 * Search the document on Solr server
	 * 
	 * @param query
	 * @return {@link AllianzBotSolrSearchDocumentResponse}
	 */
	AllianzBotSolrSearchDocumentResponse searchDocument(String query, boolean isSearch)
			throws SolrServerException, IOException, AllianzBotException;
	
	/**
	 * Update the the score for the specific document.
	 * 
	 * @param document
	 * @return 
	 */
	AllianzBotSolrCreateDocumentResponse updateScore(AllianzBotSentence document) throws SolrServerException, IOException, AllianzBotException;

}
