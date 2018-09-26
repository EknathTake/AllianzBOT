package com.allianzbot.model;

public class AllianzBotTestData {

	private String team;

	private String testCaseId;

	private String autoStatus;

	private String requirementsId;

	private String riskClass;

	private String testSetId;

	private String testLabPath;

	private String executionDate;

	private String executionStatus;

	private String failedRunCount;

	private String defectIdStr;

	private String failedStep;

	private String failedLog;

	private String screenShotPath;

	private String failureCategory;

	private long defectId;

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getAutoStatus() {
		return autoStatus;
	}

	public void setAutoStatus(String autoStatus) {
		this.autoStatus = autoStatus;
	}

	public String getRequirementsId() {
		return requirementsId;
	}

	public void setRequirementsId(String requirementsId) {
		this.requirementsId = requirementsId;
	}

	public String getRiskClass() {
		return riskClass;
	}

	public void setRiskClass(String riskClass) {
		this.riskClass = riskClass;
	}

	public String getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(String testSetId) {
		this.testSetId = testSetId;
	}

	public String getTestLabPath() {
		return testLabPath;
	}

	public void setTestLabPath(String testLabPath) {
		this.testLabPath = testLabPath;
	}

	public String getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getFailedRunCount() {
		return failedRunCount;
	}

	public void setFailedRunCount(String failedRunCount) {
		this.failedRunCount = failedRunCount;
	}

	public String getDefectIdStr() {
		return defectIdStr;
	}

	public void setDefectIdStr(String defectIdStr) {
		this.defectIdStr = defectIdStr;
	}

	public String getFailedStep() {
		return failedStep;
	}

	public void setFailedStep(String failedStep) {
		this.failedStep = failedStep;
	}

	public String getFailedLog() {
		return failedLog;
	}

	public void setFailedLog(String failedLog) {
		this.failedLog = failedLog;
	}

	public String getScreenShotPath() {
		return screenShotPath;
	}

	public void setScreenShotPath(String screenShotPath) {
		this.screenShotPath = screenShotPath;
	}

	public String getFailureCategory() {
		return failureCategory;
	}

	public void setFailureCategory(String failureCategory) {
		this.failureCategory = failureCategory;
	}

	public long getDefectId() {
		return defectId;
	}

	public void setDefectId(long defectId) {
		this.defectId = defectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllianzBotTestData [team=");
		builder.append(team);
		builder.append(", testCaseId=");
		builder.append(testCaseId);
		builder.append(", autoStatus=");
		builder.append(autoStatus);
		builder.append(", requirementsId=");
		builder.append(requirementsId);
		builder.append(", riskClass=");
		builder.append(riskClass);
		builder.append(", testSetId=");
		builder.append(testSetId);
		builder.append(", testLabPath=");
		builder.append(testLabPath);
		builder.append(", executionDate=");
		builder.append(executionDate);
		builder.append(", executionStatus=");
		builder.append(executionStatus);
		builder.append(", failedRunCount=");
		builder.append(failedRunCount);
		builder.append(", defectIdStr=");
		builder.append(defectIdStr);
		builder.append(", failedStep=");
		builder.append(failedStep);
		builder.append(", failedLog=");
		builder.append(failedLog);
		builder.append(", screenShotPath=");
		builder.append(screenShotPath);
		builder.append(", failureCategory=");
		builder.append(failureCategory);
		builder.append(", defectId=");
		builder.append(defectId);
		builder.append("]");
		return builder.toString();
	}

}
