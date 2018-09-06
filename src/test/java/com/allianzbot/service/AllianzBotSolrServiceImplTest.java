package com.allianzbot.service;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.service.impl.AllianzBotSolrServiceImpl;
import com.allianzbot.service.interfaces.IAllianzBotSolrService;
@Ignore
public class AllianzBotSolrServiceImplTest {
	
	private static final String DOCUMENT_ID = "7461532e0ca7322c4067b1ee637b0cd4";
	private IAllianzBotSolrService classUnderTest;

	@Before
	public void setUp() throws Exception {
		
		classUnderTest = new AllianzBotSolrServiceImpl();
	}

	@Test
	public void testStoreDocument() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchDocuments() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateScore() throws SolrServerException, IOException, AllianzBotException {
		//classUnderTest.updateScore(DOCUMENT_ID , 1.0);
	}

}
