package com.allianzbot.exception;

/**
 * Exception handler for the AllianzBOT.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotException extends Exception {

	/**
	 * Contains the response status code. i.e 200, 201, 400, 401, etc
	 */
	private int statusCode;

	/**
	 * Contains the error message.
	 */
	private String statusMessage;

	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = -4065237523623108333L;

	public AllianzBotException(int statusCode, String statusMessage) {
		//super(statusMessage);

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
		builder.append("AllianzBotException [statusCode=");
		builder.append(statusCode);
		builder.append(", statusMessage=");
		builder.append(statusMessage);
		builder.append("]");
		return builder.toString();
	}

}
