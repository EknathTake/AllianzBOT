package com.allianzbot.service.interfaces;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotDocument;

public interface IAllianzBotAssesmentService {

	void loadAssesmentIntoSolr();

	UpdateResponse storeDocument(AllianzBotDocument allianzBotServiceResponse) throws AllianzBotException, SolrServerException, IOException;
}
