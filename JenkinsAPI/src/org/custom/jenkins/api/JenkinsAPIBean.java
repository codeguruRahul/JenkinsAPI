package org.custom.jenkins.api;

public class JenkinsAPIBean {
private String jobName;
public String getJobName() {
	return jobName;
}
public void setJobName(String jobName) {
	this.jobName = jobName;
}
public String getJobStatus() {
	return jobStatus;
}
public void setJobStatus(String jobStatus) {
	this.jobStatus = jobStatus;
}
private String jobStatus;
}
