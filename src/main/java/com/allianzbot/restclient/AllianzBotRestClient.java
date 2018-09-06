package com.allianzbot.restclient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.utils.FileUtils;

@Controller
public class AllianzBotRestClient {

	private static final String SPACE = " ";

	private static final String COMMA = ",";

	@Value("${file.path.stopwords}")
	private String stopwordsaPath;

	@Value("${file.path.synonyms}")
	private String synonymsPath;

	Logger log = LoggerFactory.getLogger(AllianzBotRestClient.class);

	@GetMapping(path = "/load/stopwords")
	public @ResponseBody List<String> putStopwords() throws AllianzBotException, RestClientException, URISyntaxException {
		log.info("AllianzBotRestClient.putStopwords() method started.");
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://localhost:8983/solr/allianzBotCollection/schema/analysis/stopwords/english";
		List<String> stopWords = new FileUtils().loadContentFromFile(stopwordsaPath);

		//restTemplate.put(new URI(url), stopWords);
		log.info("AllianzBotRestClient.putStopwords() method finished.");
		return stopWords;
	}

	@GetMapping(path = "/load/synonyms")
	public @ResponseBody Map<String, List<String>> putSynonyms() throws AllianzBotException, IOException {
		log.info("AllianzBotRestClient.putSynonyms() method started.");
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://localhost:8983/solr/allianzBotCollection/schema/analysis/synonyms/english";

		List<String> synonyms = new FileUtils().loadContentFromFile(synonymsPath);
		Map<String, List<String>> map = new HashMap<>();
		List<String> list = null;
		for (String string : synonyms) {
			if (string.contains(SPACE)) {
				String key = string.substring(0, string.indexOf(SPACE));
				String value = string.substring(string.indexOf(SPACE) + 1, string.length());
				list = new ArrayList<>(Arrays.asList(value.split(COMMA)));
				map.put(key, list);
				
				System.out.println(key+" => "+value);
			}
		}
		//log.info("map:{}", map);
		
		//restTemplate.put(url, map);
		log.info("AllianzBotRestClient.putSynonyms() method finished.");
		return map;
	}

}
