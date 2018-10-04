package com.allianzbot.model;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Dto for Sentence along with the probability.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotSearchResponse {

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

	/**
	 * likes
	 */
	private double likes;

	private String failedLog;

	private String failedCategory;

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

	public double getLikes() {
		return likes;
	}

	public void setLikes(double likes) {
		this.likes = likes;
	}

	public String getFailedLog() {
		return failedLog;
	}

	public void setFailedLog(String failedLog) {
		this.failedLog = failedLog;
	}

	public String getFailedCategory() {
		return failedCategory;
	}

	public void setFailedCategory(String failedCategory) {
		this.failedCategory = failedCategory;
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
		builder.append(", likes=");
		builder.append(likes);
		builder.append(", failedLog=");
		builder.append(failedLog);
		builder.append(", failedCategory=");
		builder.append(failedCategory);
		builder.append("]");
		return builder.toString();
	}
	
	public AllianzBotSearchResponse() {}	

	public AllianzBotSearchResponse(AllianzBotSearchResponseBuilder builder) {
		this.id = builder.id;
		this.question = builder.question;
		this.answer = builder.answer;
		this.score = builder.score;
		this.likes = builder.likes;
		this.failedLog = builder.failedLog;
		this.failedCategory = builder.failedCategory;
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

		private String failedLog;

		private String failedCategory;

		public AllianzBotSearchResponseBuilder(AllianzBotSearchResponse response) {
			this.id = response.id;
			this.question = response.question;
			this.answer = response.answer;
			this.score = response.score;
			this.likes = response.likes;
			this.failedLog = response.failedLog;
			this.failedCategory = response.failedCategory;
		}
		public AllianzBotSearchResponseBuilder() {}
		
		public AllianzBotSearchResponseBuilder(String id, String question, String answer, double score, double likes,
				String failedLog, String failedCategory) {
			this.id = id;
			this.question = question;
			this.answer = answer;
			this.score = score;
			this.likes = likes;
			this.failedLog = failedLog;
			this.failedCategory = failedCategory;
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

		public AllianzBotSearchResponseBuilder setFailedLog(String failedLog) {
			this.failedLog = failedLog;
			return this;
		}

		public AllianzBotSearchResponseBuilder setFailedCategory(String failedCategory) {
			this.failedCategory = failedCategory;
			return this;
		}

		public AllianzBotSearchResponse build() {
			return new AllianzBotSearchResponse(this);
		}

	}
}
