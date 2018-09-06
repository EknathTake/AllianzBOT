package com.allianzbot.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.allianzbot.service.impl.AllianzBotTokenizerServiceImpl;
import com.allianzbot.service.interfaces.IAllianzBotTokenizerService;

/**
 * Testcase for the service layer {@link AllianzBotTokenizerServiceImpl}
 * 
 * @author eknath.take
 *
 */
@Ignore
public class AllianzBotTokenizerServiceImplTest {

	private IAllianzBotTokenizerService classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new AllianzBotTokenizerServiceImpl();
	}

	/**
	 * Testcase : Test the positive scenario for the tokenizeString
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTokenizeString() throws IOException {

		List<String> actualResponse = classUnderTest.tokenizeString("Demo text for Apache Tika");

		assertEquals(4, actualResponse.size());
	}

}
