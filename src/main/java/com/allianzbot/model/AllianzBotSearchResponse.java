package com.allianzbot.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

/**
 * Dto for Sentence along with the probability.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotSearchResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2619829419136110636L;

	@NotEmpty(message = "Null id not acceptable.")
	private String id;

	/**
	 * question
	 */
	@NotEmpty(message = "Null question not acceptable.")
	private String question;

	/**
	 * answer
	 */
	@NotEmpty(message = "Null answer not acceptable.")
	private String answer;

	/**
	 * score
	 */
	// @Min(value = 0L, message = "Invalid value for the score")
	private double score;

	private AllianzBotTestCenterData allianzBotTestCenterData;

	/**
	 * likes
	 */
	private double likes;

	public String getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

	public double getScore() {
		return score;
	}

	public double getLikes() {
		return likes;
	}

	public AllianzBotTestCenterData getAllianzBotTestCenterData() {
		return allianzBotTestCenterData;
	}

	public AllianzBotSearchResponse() {
	}

	public AllianzBotSearchResponse(AllianzBotSearchResponseBuilder builder) {
		this.id = builder.id;
		this.question = builder.question;
		this.answer = builder.answer;
		this.score = builder.score;
		this.likes = builder.likes;
		this.allianzBotTestCenterData = builder.allianzBotTestCenterData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotSearchResponse [id=");
		builder.append(id);
		builder.append(", question=");
		builder.append(question);
		builder.append(", answer=");
		builder.append(answer);
		builder.append(", score=");
		builder.append(score);
		builder.append(", allianzBotTestCenterData=");
		builder.append(allianzBotTestCenterData);
		builder.append(", likes=");
		builder.append(likes);
		builder.append("]");
		return builder.toString();
	}

	public static class AllianzBotSearchResponseBuilder {

		private String id;

		/**
		 * question
		 */
		private String question;

		/**
		 * answer
		 */
		private String answer;

		/**
		 * score
		 */
		private double score;

		/**
		 * likes
		 */
		private double likes;

		private AllianzBotTestCenterData allianzBotTestCenterData;

		public AllianzBotSearchResponseBuilder(AllianzBotSearchResponse response) {
			this.id = response.id;
			this.question = response.question;
			this.answer = response.answer;
			this.score = response.score;
			this.likes = response.likes;
			this.allianzBotTestCenterData = response.allianzBotTestCenterData;
		}

		public AllianzBotSearchResponseBuilder() {
		}

		public AllianzBotSearchResponseBuilder setId(String id) {
			this.id = id;
			return this;
		}

		public AllianzBotSearchResponseBuilder setQuestion(String question) {
			this.question = question;
			return this;
		}

		public AllianzBotSearchResponseBuilder setAnswer(String answer) {
			this.answer = answer;
			return this;
		}

		public AllianzBotSearchResponseBuilder setScore(double score) {
			this.score = score;
			return this;
		}

		public AllianzBotSearchResponseBuilder setLikes(double likes) {
			this.likes = likes;
			return this;
		}

		public AllianzBotSearchResponseBuilder setAllianzBotTestCenterData(
				AllianzBotTestCenterData allianzBotTestCenterData) {
			this.allianzBotTestCenterData = allianzBotTestCenterData;
			return this;
		}

		public AllianzBotSearchResponse build() {
			return new AllianzBotSearchResponse(this);
		}

	}
}
