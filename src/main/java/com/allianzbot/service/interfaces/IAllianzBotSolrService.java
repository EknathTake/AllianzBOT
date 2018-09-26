package com.allianzbot.service.interfaces;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.model.AllianzBotSentence;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;

/**
 * Solr service layer
 * 
 * @author eknath.take
 *
 */
public interface IAllianzBotSolrService {

	/**
	 * Create document in Solr server.
	 * 
	 * @param allianzBotServiceResponse
	 * @return {@link UpdateResponse}
	 * @throws SolrServerException
	 * @throws IOException
	 */
	UpdateResponse storeDocument(AllianzBotDocument allianzBotServiceResponse) throws SolrServerException, IOException;

	/**
	 * Search the user keyuword in Solr server.
	 * 
	 * @param keywords
	 * @return SolrDocumentList
	 */
	AllianzBotSolrSearchDocumentResponse searchDocuments(String query, boolean isSearch)
			throws SolrServerException, IOException, AllianzBotException;

	/**
	 * Update the the score for the specific document.
	 * 
	 * @param document
	 */
	void updateScore(AllianzBotSentence document) throws SolrServerException, IOException, AllianzBotException;

}
