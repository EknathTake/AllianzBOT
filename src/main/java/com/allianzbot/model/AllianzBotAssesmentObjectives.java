package com.allianzbot.model;

import java.io.Serializable;

public class AllianzBotAssesmentObjectives implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 819269240181206442L;

	private String objective;

	private boolean isChecked;

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotAssesmentObjectives [objective=");
		builder.append(objective);
		builder.append(", isChecked=");
		builder.append(isChecked);
		builder.append("]");
		return builder.toString();
	}

}
