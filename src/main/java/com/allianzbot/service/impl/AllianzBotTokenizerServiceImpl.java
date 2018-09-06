package com.allianzbot.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import com.allianzbot.service.interfaces.IAllianzBotTokenizerService;

/**
 * Service for Tokenization the contents extracted from Document.
 * @author eknath.take
 *
 */
@Service("allianzBotTokenizerService")
public class AllianzBotTokenizerServiceImpl implements IAllianzBotTokenizerService{

	@Override
	public List<String> tokenizeString(String content) throws IOException {
		List<String> result = new ArrayList<>();
		try(Analyzer analyzer = new StandardAnalyzer(StandardAnalyzer.ENGLISH_STOP_WORDS_SET)){
			TokenStream stream = analyzer.tokenStream(null, new StringReader(content));
			stream.reset();
			while (stream.incrementToken()) {
				result.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
		}
		return result;
	}

	/*public String tokenizeString(Analyzer analyzer, String str) {
		StringBuilder finalResponse = new StringBuilder();
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(str));
			stream.reset();
			while (stream.incrementToken()) {
				finalResponse.append(stream.getAttribute(CharTermAttribute.class).toString()).append(" ");
			}
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new RuntimeException(e);
		}
		
		return finalResponse.toString();

	}*/

}
