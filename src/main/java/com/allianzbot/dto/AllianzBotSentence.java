package com.allianzbot.dto;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Dto for Sentence along with the probability.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotSentence implements Serializable {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 3411583878782802899L;

	@NotEmpty
	private String id;

	/**
	 * question
	 */
	@NotEmpty
	private String question;

	/**
	 * answer
	 */
	@NotEmpty
	private String answer;

	/**
	 * score
	 */
	@Min(value = 0L, message = "Invalid value for the score")
	private double score;

	/**
	 * hits
	 */
	@Min(value = 0L, message = "Invalid value for the hits")
	private double hits;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getHits() {
		return hits;
	}

	public void setHits(double hits) {
		this.hits = hits;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotSentence [id=");
		builder.append(id);
		builder.append(", question=");
		builder.append(question);
		builder.append(", answer=");
		builder.append(answer);
		builder.append(", score=");
		builder.append(score);
		builder.append(", hits=");
		builder.append(hits);
		builder.append("]");
		return builder.toString();
	}
}
