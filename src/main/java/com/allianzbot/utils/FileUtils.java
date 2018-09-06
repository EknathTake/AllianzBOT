package com.allianzbot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import com.allianzbot.exception.AllianzBotException;

/**
 * Utility class to load all the stop words from the file.
 * 
 * @author eknath.take
 *
 */
public class FileUtils {

	private static Logger log = Logger.getLogger(FileUtils.class);

	private static List<String> STOPWORDS = new ArrayList<>();

	/**
	 * Loads all the stop words from the file.
	 * 
	 * @return stopwords
	 * @throws AllianzBotException
	 */
	public List<String> loadContentFromFile(String path) throws AllianzBotException {

		ClassPathResource resource = new ClassPathResource(path);
		try (InputStream is = resource.getInputStream();) {
			STOPWORDS = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
		} catch (IOException e) {
			log.error("Exception Occured in StopwordsUtils :{}", e);
			throw new AllianzBotException(404, "File located on following path: "+path+" does not found.");
		}
		
		return STOPWORDS;
	}

	/**
	 * removes all the stopwords from the sentence.
	 * 
	 * @param sentence
	 * @return
	 * @throws AllianzBotException
	 */
	public static String removeStopWords(String sentence) throws AllianzBotException {
		if (STOPWORDS.size() == 0)
			new FileUtils().loadContentFromFile("/solrconfig/stopwords.txt");
		if (StringUtils.isNotEmpty(sentence)) {

			String collectQuery = Stream.of(sentence.split(" "))
					.filter(Objects::nonNull)
					.filter(keyword -> !STOPWORDS.contains(keyword))
					.filter(keyword -> !containsEqualsIgnoreCase(STOPWORDS, keyword))
					.collect(Collectors.joining(" "));
			return (StringUtils.isNotEmpty(collectQuery) ? collectQuery : sentence)
					.replaceAll(AllianzBotConstants.AB_SPECIAL_CHARACTERS, " ");
		}

		return sentence;
	}

	private static boolean containsEqualsIgnoreCase(Collection<String> c, String s) {
		for (String str : c) {
			if (s.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}
}
