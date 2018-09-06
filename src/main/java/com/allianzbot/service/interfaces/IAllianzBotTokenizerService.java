package com.allianzbot.service.interfaces;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Analyzer Class is the basic Class defined in Lucene Core particularly
 * specialized for direct use for parsing queries and maintaining the queries.
 * Different methods are available in the Analyzer Class so that we can easily
 * go with the analyzing tasks using a wide range of analyzer options provided
 * by the Lucene.</>
 * 
 * <p>
 * Analyzer is something like policy to extract index terms from the token-able
 * text.So, this can interpret with different sorts of text value and built a
 * TokenStreams for that.So, the queryString as an input from us or a stored
 * data is analyzed through extracting of index term from them using the
 * preferred policy of Analyzer Class. Literally, it is the one to analyze the
 * text. And this can be the prerequisite for indexing and searching process in
 * Lucene. It is defined under org.apache.lucene.analysis as a abstract class.
 * </p>
 * 
 * @author eknath.take
 *
 */
public interface IAllianzBotTokenizerService {

	/**
	 * Tokenize the given String. Also removes the stopwords from the String.
	 * 
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public List<String> tokenizeString(String content) throws IOException;

}
