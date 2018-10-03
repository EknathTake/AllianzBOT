package com.allianzbot.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AllianzBotResponseStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1878453887504085845L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date timestamp = getDateOfAsianTimeZone(new Date());

	/**
	 * Contains the response status code. i.e 200, 201, 400, 401, etc
	 */
	private int statusCode;

	/**
	 * Contains the error message.
	 */
	private String message;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = getDateOfAsianTimeZone(timestamp);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AllianzBotResponseStatus() {
		super();
	}

	public AllianzBotResponseStatus(Date timestamp, int statusCode, String message) {
		super();
		this.timestamp = getDateOfAsianTimeZone(timestamp);
		this.statusCode = statusCode;
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotResponseStatus [timestamp=");
		builder.append(timestamp);
		builder.append(", statusCode=");
		builder.append(statusCode);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Utility function to convert java Date to TimeZone format
	 * 
	 * @param date
	 * @param format
	 * @param timeZone
	 * @return
	 * @throws ParseException
	 */
	public Date getDateOfAsianTimeZone(Date date) {
		String timeZone = "Asia/Calcutta";
		final String format = "dd-MM-yyyy HH:mm:ss";
		// null check
		if (date == null)
			return new Date();
		// create SimpleDateFormat object with input format
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		SimpleDateFormat parser = new SimpleDateFormat(format);
		// default system timezone if passed null or empty
		if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
			timeZone = Calendar.getInstance().getTimeZone().getID();
		}
		// set timezone to SimpleDateFormat
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		// return Date in required format with timezone
		try {
			return parser.parse(sdf.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

}
