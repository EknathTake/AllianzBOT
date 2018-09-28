package com.allianzbot.model;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class AllianzBotAssesmentQuestions {

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
