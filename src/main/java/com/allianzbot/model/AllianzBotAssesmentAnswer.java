package com.allianzbot.model;

/**
 * Bot Answer
 * 
 * @author eknath.take
 *
 */
public class AllianzBotAssesmentAnswer {

	private long answerId;

	private long questionId;

	private String actualAnswer;

	public long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(long answerId) {
		this.answerId = answerId;
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getActualAnswer() {
		return actualAnswer;
	}

	public void setActualAnswer(String actualAnswer) {
		this.actualAnswer = actualAnswer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotAnswer [answerId=");
		builder.append(answerId);
		builder.append(", questionId=");
		builder.append(questionId);
		builder.append(", actualAnswer=");
		builder.append(actualAnswer);
		builder.append("]");
		return builder.toString();
	}

}
