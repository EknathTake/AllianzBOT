package com.allianzbot.service.impl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.allianzbot.exception.AllianzBotException;
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
	public void loadAssesmentIntoSolr() {

	}

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
			
				if (CollectionUtils.isNotEmpty(columns) && i>0) {
					if (columns.size() == 3) {
						/** for asssment answers */
						doc = new SolrInputDocument();
						doc.addField(AllianzBotConstants.ABAssesmentAnswers.AB_ANSWER_ID, new Long(columns.get(0)));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID, new Long(columns.get(1)));
						doc.addField(AllianzBotConstants.ABAssesmentAnswers.AB_CORRECT_ANSWER, columns.get(2));
					} else if (columns.size() == 5) {
						/** for asssment questions */
						doc = new SolrInputDocument();
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION_ID, new Long(columns.get(0)));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_QUESTION, columns.get(1));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_OBJECTIVES, columns.get(2));
						doc.addField(AllianzBotConstants.ABAssesmentQuestion.AB_IS_MULTIANSWERS, new Boolean(columns.get(3)));
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

}
