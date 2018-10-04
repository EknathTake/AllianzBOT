package com.allianzbot.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotSearchResponse;
import com.allianzbot.service.impl.AllianzBotOpenNlpServiceImpl;
import com.allianzbot.service.interfaces.IAllianzBotOpenNlpService;

import opennlp.tools.util.InvalidFormatException;

@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitWebConfig(locations = "classpath:test-servlet-context.xml")
public class AllianzBotOpenNlpServiceImplTest {

	private IAllianzBotOpenNlpService classUnderTest;

	private static final String BASE_PATH = "src/main/resources/training/";

	@Before
	public void setUp() throws Exception {
		classUnderTest = new AllianzBotOpenNlpServiceImpl();
	}

	@Test
	public void testSentenceDetect() throws InvalidFormatException, IOException, AllianzBotException {

		 List<AllianzBotSearchResponse> actualOutput = classUnderTest.sentenceDetect("Hi. How are you? This is Eknath.");
		assertEquals(2, actualOutput.size());
	}

	@Test

	public void testTokenize() throws InvalidFormatException, IOException, AllianzBotException {
		String[] actualOutput = classUnderTest.tokenize("Hi. How are you? This is Eknath. \n \r \t hey");
		assertEquals(11, actualOutput.length);
	}

	@Test
	public void testFindName() throws IOException, AllianzBotException {
		String sentence = "Mike is a good person. Smith and Eknath both are working in Accenture.";
		String[] actualOutput = classUnderTest.findName(sentence);

		assertEquals("Mike", actualOutput[0]);
		assertEquals("Smith", actualOutput[1]);
		// assertEquals("Eknath", actualOutput[2]);
	}

	@Test
	public void testPartOfSpeechTagger() throws IOException, AllianzBotException {
		classUnderTest.partOfSpeechTagger("Hi Bot, tell me anything about BenelusOne Project.?",true);
	}

	@Test @Ignore
	public void testTrainNameFinderModel() throws IOException, AllianzBotException {
		classUnderTest.trainNameFinderModel(BASE_PATH + "en-sample-org.train", BASE_PATH + "en-sample-org.bin", "en",
				"organization");
	}

}
