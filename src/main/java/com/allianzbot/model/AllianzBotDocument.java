package com.allianzbot.model;

/**
 * Response object for AllianzBot for peocess layer. It will contains the
 * extracted content of the particular artifact.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotDocument {

	private String id;

	private Object content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotDocument [id=");
		builder.append(id);
		builder.append(", content=");
		builder.append(content);
		builder.append("]");
		return builder.toString();
	}

}
