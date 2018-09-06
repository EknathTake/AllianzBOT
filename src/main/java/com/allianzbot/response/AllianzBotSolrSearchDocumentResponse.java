package com.allianzbot.response;

import java.util.List;
import com.allianzbot.dto.AllianzBotResponseStatus;
import com.allianzbot.dto.AllianzBotSentence;

/**
 * Response object for AllianzBot. It will contains the extracted content of the
 * particular artifact.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotSolrSearchDocumentResponse {

	private List<AllianzBotSentence> documents;

	/**
	 * If any exception occured it will give us the information regarding exception.
	 */
	private AllianzBotResponseStatus error;

	public List<AllianzBotSentence> getDocuments() {
		return documents;
	}

	public void setDocuments(List<AllianzBotSentence> documents) {
		this.documents = documents;
	}

	public AllianzBotResponseStatus getError() {
		return error;
	}

	public void setError(AllianzBotResponseStatus error) {
		this.error = error;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotControllerResponse [documents=");
		builder.append(documents);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}

}