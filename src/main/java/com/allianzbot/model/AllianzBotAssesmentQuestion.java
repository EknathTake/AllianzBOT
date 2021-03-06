package com.allianzbot.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Bot Question
 * 
 * @author eknath.take
 *
 */
public class AllianzBotAssesmentQuestion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5480256652381389032L;

	private long questionId;

	private String question;

	private AllianzBotAssesmentObjectives[] objectives;

	private boolean isMultiAnswer;

	private String[] userAnswer;

	private String topic;

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public AllianzBotAssesmentObjectives[] getObjectives() {
		return objectives;
	}

	public void setObjectives(AllianzBotAssesmentObjectives[] objectives) {
		this.objectives = objectives;
	}

	public boolean getIsMultiAnswer() {
		return isMultiAnswer;
	}

	public void setIsMultiAnswer(boolean isMultiAnswer) {
		this.isMultiAnswer = isMultiAnswer;
	}

	public String[] getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String[] userAnswer) {
		this.userAnswer = userAnswer;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotQuestion [questionId=");
		builder.append(questionId);
		builder.append(", question=");
		builder.append(question);
		builder.append(", objectives=");
		builder.append(Arrays.toString(objectives));
		builder.append(", isMultiAnswer=");
		builder.append(isMultiAnswer);
		builder.append(", userAnswer=");
		builder.append(userAnswer);
		builder.append(", topic=");
		builder.append(topic);
		builder.append("]");
		return builder.toString();
	}

}
