package com.allianzbot.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

public class AllianzBotAssesmentQuestions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1832151999712402276L;
	private Map<Long, AllianzBotAssesmentQuestion> allianzBotQuestions;

	public Map<Long, AllianzBotAssesmentQuestion> getAllianzBotQuestions() {
		return allianzBotQuestions;
	}

	public void setAllianzBotQuestions(Map<Long, AllianzBotAssesmentQuestion> allianzBotQuestions) {
		this.allianzBotQuestions = allianzBotQuestions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotQuestions [allianzBotQuestions=");
		builder.append(allianzBotQuestions);
		builder.append("]");
		return builder.toString();
	}

}
