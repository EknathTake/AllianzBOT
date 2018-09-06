package com.allianzbot.exception;

public class AllianzBotRunTimeException extends RuntimeException {

	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = -8722242078379291424L;

	/**
	 * Contains the response status code. i.e 200, 201, 400, 401, etc
	 */
	private int statusCode;

	/**
	 * Contains the error message.
	 */
	private String statusMessage;

	public AllianzBotRunTimeException(int statusCode, String statusMessage) {
		// super(statusMessage);

		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotRunTimeException [statusCode=");
		builder.append(statusCode);
		builder.append(", statusMessage=");
		builder.append(statusMessage);
		builder.append("]");
		return builder.toString();
	}

}
