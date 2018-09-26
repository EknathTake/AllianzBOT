package com.allianzbot.service.interfaces;

import java.io.IOException;
import java.util.List;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotSentence;

import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InvalidFormatException;

/**
 * Service layer for processing of natural language text. It includes a sentence
 * detector, a tokenizer, a name finder, a parts-of-speech (POS) tagger, a
 * chunker, and a parser.
 * 
 * @author eknath.take
 *
 */
public interface IAllianzBotOpenNlpService {

	/**
	 * Sentence detector is for detecting sentence boundaries.
	 * 
	 * @param paragraph
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws AllianzBotException
	 */
	List<AllianzBotSentence> sentenceDetect(String paragraph)
			throws InvalidFormatException, IOException, AllianzBotException;

	/**
	 * Tokens are usually words which are separated by space, but there are
	 * exceptions. For example, "isn't" gets split into "is" and "n't, since it is a
	 * a brief format of "is not". Our sentence is separated into the following
	 * tokens:
	 * 
	 * <pre>
	 *  <code>
	 * Hi .
	 * How are you ? 
	 * This is Eknath .
	 * </code>
	 * </pre>
	 * 
	 * @param string
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws AllianzBotException
	 */
	String[] tokenize(String string) throws InvalidFormatException, IOException, AllianzBotException;

	/**
	 * By its name, name finder just finds names in the context. Check out the
	 * following example to see what name finder can do. It accepts an array of
	 * strings, and find the names inside.
	 * 
	 * @param sentence
	 * @return
	 * @throws IOException
	 * @throws AllianzBotException
	 */
	String[] findName(String sentence) throws IOException, AllianzBotException;

	/**
	 * Part of Speech Tagger. for example <br>
	 * Hi._NNP How_WRB are_VBP you?_JJ This_DT is_VBZ Eknath._NNP
	 * 
	 * @param paragraph
	 * @param isSentenceLevel by default the sentence detecter is disabled.
	 * @return
	 * @throws IOException
	 * @throws AllianzBotException
	 */
	POSSample partOfSpeechTagger(String paragraph, boolean isSentenceDetector) throws IOException, AllianzBotException;

	/**
	 * <p>
	 * Method utility to train the model which is useful for Named Entity
	 * Recognition. In the input file the training data must be in following
	 * format.</p<br>
	 * 
	 * <pre>
	 * 	{@code
	 * 	<START:person> Mike <END> is senior programming manager and
		<START:person> Rama <END> is a clerk both are working at Accenture. 
	 * 	}
	 * </pre>
	 * 
	 * Note: Above example is only for Person model if you want to train for another
	 * modet then you can to custom model. i.e
	 * {@code<START:ogranization> Accenture <END>}
	 * 
	 * @param inputFile    contains the training data
	 * @param modelFile    Name of the destination file. Once the training has been
	 *                     completed. The result will be stored in this file.
	 * @param languageCode language code. i.e en, de.. etc
	 * @param modelType    The type of the model i.e. person, organization...etc
	 * @throws IOException
	 */
	void trainNameFinderModel(String inputFile, String modelFile, String languageCode, String modelType)
			throws IOException;

	/**
	 * <p>
	 * Lemmatizer is a Natural Language Processing tool that aims to remove any
	 * changes in form of the word like tense, gender, mood, etc. and return
	 * dictionary or base form of word.
	 * </p>
	 * <p>
	 * In Apache OpenNLP, Lemmatizer returns base or dictionary form of the word
	 * (usually called lemma) when it is provided with word and its Parts-Of-Speech
	 * tag. For a given word, there could exist many lemmas, but given the
	 * Parts-Of-Speech tag also, the number could be narrowed down to almost one,
	 * and the one is the more accurate as the context to the word is provided in
	 * the form of postag.
	 * </p>
	 * 
	 * @param paragraph
	 * @return
	 * @throws IOException
	 * @throws AllianzBotException
	 */
	String[] lemmatization(String paragraph) throws IOException, AllianzBotException;

}
