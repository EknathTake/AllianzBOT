package com.allianzbot.dto;

import java.io.Serializable;

/**
 * DTO to hold the sentence, POS tags of the sentence and probability of the
 * tags.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotPartOfSpeech implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 4342044983614819023L;

	private String sentence;

	private String tags;

	private double probability;

	public AllianzBotPartOfSpeech() {
		super();
	}

	public AllianzBotPartOfSpeech(String sentence, String tags, double probability) {
		super();
		this.sentence = sentence;
		this.tags = tags;
		this.probability = probability;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[sentence=");
		builder.append(sentence);
		builder.append(", tags=");
		builder.append(tags);
		builder.append(", probability=");
		builder.append(probability);
		builder.append("]");
		return builder.toString();
	}

}
