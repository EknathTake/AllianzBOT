package com.allianzbot.model;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7702575245752451302L;

	private String userId;

	private String username;

	private String password;

	private String role;

	private SkillInfo skillInfo;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public SkillInfo getSkillInfo() {
		return skillInfo;
	}

	public void setSkillInfo(SkillInfo skillInfo) {
		this.skillInfo = skillInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [userId=");
		builder.append(userId);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", role=");
		builder.append(role);
		builder.append(", skillInfo=");
		builder.append(skillInfo);
		builder.append("]");
		return builder.toString();
	}

}
