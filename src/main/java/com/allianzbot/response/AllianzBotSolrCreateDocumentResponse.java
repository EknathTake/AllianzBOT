package com.allianzbot.response;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.model.AllianzBotResponseStatus;

/**
 * Response object for AllianzBot for peocess layer. It will contains the
 * extracted content of the particular artifact.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotSolrCreateDocumentResponse implements Serializable {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 8165853140832268859L;

	/**
	 * Contains the content and metadata of the document
	 */
	private AllianzBotDocument allianzBotDocument;

	/**
	 * Contains the response status.
	 */
	private AllianzBotResponseStatus status;

	public AllianzBotDocument getAllianzBotDocument() {
		return allianzBotDocument;
	}

	public void setAllianzBotDocument(AllianzBotDocument allianzBotDocument) {
		this.allianzBotDocument = allianzBotDocument;
	}

	public AllianzBotResponseStatus getStatus() {
		return status;
	}

	public void setStatus(AllianzBotResponseStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotProcessResponse [allianzBotDocument=");
		builder.append(allianzBotDocument);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}

}
