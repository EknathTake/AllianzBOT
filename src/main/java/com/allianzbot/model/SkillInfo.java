package com.allianzbot.model;

import java.io.Serializable;
import java.util.Arrays;

public class SkillInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9108953469926330283L;

	private String[] primarySkills;

	private SkillType skillType;

	private SkillProfficiency skillProfficiency;

	public String[] getPrimarySkills() {
		return primarySkills;
	}

	public void setPrimarySkills(String[] primarySkills) {
		this.primarySkills = primarySkills;
	}

	public SkillType getSkillType() {
		return skillType;
	}

	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}

	public SkillProfficiency getSkillProfficiency() {
		return skillProfficiency;
	}

	public void setSkillProfficiency(SkillProfficiency skillProfficiency) {
		this.skillProfficiency = skillProfficiency;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SkillInfo [primarySkills=");
		builder.append(Arrays.toString(primarySkills));
		builder.append(", skillType=");
		builder.append(skillType);
		builder.append(", skillProfficiency=");
		builder.append(skillProfficiency);
		builder.append("]");
		return builder.toString();
	}

}
