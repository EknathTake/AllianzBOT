package com.allianzbot.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotSentence;
import com.allianzbot.service.interfaces.IAllianzBotOpenNlpService;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

/**
 * TODO: need to work on static path, it's hard-coded for now. Service
 * Implementation for Natural Language Processing
 * 
 * @author eknath.take
 *
 */

@Service("allianzBotOpenNlpService")
public class AllianzBotOpenNlpServiceImpl implements IAllianzBotOpenNlpService {

	private Logger log = LoggerFactory.getLogger(AllianzBotOpenNlpServiceImpl.class);

	private ClassPathResource resource;

	@Override
	public List<AllianzBotSentence> sentenceDetect(String paragraph)
			throws InvalidFormatException, IOException, AllianzBotException {

		// load the training data
		resource = new ClassPathResource("/opennlp/en-sent.bin");
		try (InputStream is = resource.getInputStream();) {
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);

			String[] sentences = sdetector.sentDetect(paragraph);
			double[] probs = sdetector.getSentenceProbabilities();

			return IntStream.range(0, sentences.length).filter(i -> StringUtils.isNotEmpty(sentences[i]))
					.mapToObj(i -> {
						AllianzBotSentence allianzBotSentence = new AllianzBotSentence();
						allianzBotSentence.setScore(probs[i]);
						allianzBotSentence.setAnswer(sentences[i]);
						return allianzBotSentence;
					}).collect(Collectors.toList());

		} catch (FileNotFoundException e) {
			log.warn("Exception occcured in AllianzBotOpenNlpServiceImpl.sentenceDetect() {}", e);
			throw new AllianzBotException(404, "The requested resource could not be found");
		}

	}

	@Override
	public String[] tokenize(final String string) throws InvalidFormatException, IOException, AllianzBotException {

		// load the training data
		resource = new ClassPathResource("/opennlp/en-token.bin");
		try (InputStream is = resource.getInputStream();) {
			TokenizerModel model = new TokenizerModel(is);
			Tokenizer tokenizer = new TokenizerME(model);
			return tokenizer.tokenize(string);

		} catch (FileNotFoundException e) {
			log.warn("Exception occcured in AllianzBotOpenNlpServiceImpl.tokenize() {}", e);
			throw new AllianzBotException(404, "The requested resource could not be found");
		}

	}

	@Override
	public String[] findName(String sentence) throws IOException, AllianzBotException {

		String[] tokens = tokenize(sentence);
		// load the training data
		resource = new ClassPathResource("/opennlp/en-ner-person.bin");
		try (InputStream is = resource.getInputStream();) {
			TokenNameFinderModel model = new TokenNameFinderModel(is);
			NameFinderME nameFinder = new NameFinderME(model);

			Span[] nameSpans = nameFinder.find(tokens);
			String[] names = Span.spansToStrings(nameSpans, tokens);
			return names;
		} catch (FileNotFoundException e) {
			log.warn("Exception occcured in AllianzBotOpenNlpServiceImpl.findName() {}", e);
			throw new AllianzBotException(404, "The requested resource could not be found");
		}

	}

	@Override
	public POSSample partOfSpeechTagger(String paragraph, boolean isSentenceDetector)
			throws IOException, AllianzBotException {

		// Loading Parts of speech-maxent model
		resource = new ClassPathResource("/opennlp/en-pos-maxent.bin");
		try (InputStream inputStream = resource.getInputStream();) {
			POSModel model = new POSModel(inputStream);

			// Instantiating POSTaggerME class
			POSTaggerME tagger = new POSTaggerME(model);

			// tokenize the paragraph
			String[] tokens = tokenize(paragraph);

			// tagger.topKSequences(tokens);

			// Generating tags
			String[] tags = tagger.tag(tokens);

			// probability
			// double[] prob = tagger.probs();

			/** Sample code */
			/*
			 * List<AllianzBotPartOfSpeech> result = IntStream.range(0, tokens.length)
			 * .mapToObj(i -> new AllianzBotPartOfSpeech(tokens[i], tags[i], prob[i]))
			 * .filter(allianzBotPOS -> !StringUtils.equals(allianzBotPOS.getTags(), "."))
			 * .collect(Collectors.toList()).stream()
			 * .sorted(Comparator.comparing(AllianzBotPartOfSpeech::getProbability).reversed
			 * ()) .collect(Collectors.toList());
			 */
			// log.info("In AllianzBotOpenNlpServiceImpl.partOfSpeechTagger result:
			// {}",result);

			// Instantiating the POSSample class

			POSSample posSample = new POSSample(tokens, tags);
			return posSample;

		} catch (FileNotFoundException e) {
			log.warn("Exception occcured in AllianzBotOpenNlpServiceImpl.partOfSpeechTagger() {}", e);
			throw new AllianzBotException(404, "The requested resource could not be found");
		}
	}

	@Override
	public void trainNameFinderModel(String inputFile, String modelFile, String languageCode, String modelType)
			throws IOException {
		Charset charset = Charset.forName("UTF-8");

		MarkableFileInputStreamFactory factory = new MarkableFileInputStreamFactory(new File(inputFile));
		ObjectStream<String> lineStream = new PlainTextByLineStream(factory, charset);

		try (ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
				OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));) {

			TokenNameFinderModel model = NameFinderME.train(languageCode, modelType, sampleStream,
					TrainingParameters.defaultParams(), new TokenNameFinderFactory());

			model.serialize(modelOut);
		}
	}

	@Override
	public String[] lemmatization(String paragraph) throws IOException, AllianzBotException {

		// Loading Parts of speech-maxent model
		resource = new ClassPathResource("/opennlp/en-pos-maxent.bin");
		try (InputStream inputStream = resource.getInputStream();
				InputStream dictLemmatizer = new ClassPathResource("/en-lemmatizer.txt").getInputStream();) {
			
			POSModel model = new POSModel(inputStream);

			// Instantiating POSTaggerME class
			POSTaggerME tagger = new POSTaggerME(model);

			// tokenize the paragraph
			String[] tokens = tokenize(paragraph);

			// tagger.topKSequences(tokens);

			// Generating tags
			String[] tags = tagger.tag(tokens);

			// loading the dictionary to input stream

			// loading the lemmatizer with dictionary
			DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);

			// finding the lemmas
			String[] lemmas = lemmatizer.lemmatize(tokens, tags);

			// printing the results
			/*System.out.println("\nPrinting lemmas for the given sentence...");
			System.out.println("WORD -POSTAG : LEMMA");
			for (int i = 0; i < tokens.length; i++) {
				System.out.println(tokens[i] + " -" + tags[i] + " : " + lemmas[i]);
			}*/

			return lemmas;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String[0];
	}

}
