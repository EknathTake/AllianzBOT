package com.allianzbot.solrclient.initializer;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("solrInitializer")
public class SolrClientInitializer {
	
	@Value("${url.solr.collection}")
	private String SOLR_COLLECTION1;

	@Value("${url.solr.assesmentCollection}")
	private String assesmentCollection;
	
	public SolrClient client, client1;

	private Logger log = LoggerFactory.getLogger(SolrClientInitializer.class);
	
	@PostConstruct
	public void initSolr() {
		log.info("SOLR server is starting.");
		client = new HttpSolrClient.Builder()
				.withBaseSolrUrl(SOLR_COLLECTION1)
				.build();
		
		client1 = new HttpSolrClient.Builder()
				.withBaseSolrUrl(assesmentCollection)
				.build();
		
		log.info("SOLR server is started.");
	}

	@PreDestroy
	public void destroySolr() throws IOException {

		client.close();
		client1.close();
		log.info("SOLR server is stopped.");
	}
	
}
