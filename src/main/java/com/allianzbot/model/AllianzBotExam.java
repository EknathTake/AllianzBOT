package com.allianzbot.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AllianzBotExam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8227557378092612374L;

	private LocalDateTime startTime;

	private LocalDateTime finishTime;

	private User user;

	private double score;

	private String topic;

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(LocalDateTime finishTime) {
		this.finishTime = finishTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
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
		builder.append("AllianzBotExam [startTime=");
		builder.append(startTime);
		builder.append(", finishTime=");
		builder.append(finishTime);
		builder.append(", user=");
		builder.append(user);
		builder.append(", score=");
		builder.append(score);
		builder.append(", topic=");
		builder.append(topic);
		builder.append("]");
		return builder.toString();
	}

}
