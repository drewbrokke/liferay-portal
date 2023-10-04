package com.liferay.gradle.plugins.workspace.task;

import org.gradle.api.DefaultTask;

/**
 * @author Drew Brokke
 */
public class DrewExpectFailureTask extends DefaultTask {

	public String getExpectedErrorMessage() {
		return expectedErrorMessage;
	}

	public void setExpectedErrorMessage(String expectedErrorMessage) {
		this.expectedErrorMessage = expectedErrorMessage;
	}

	private String expectedErrorMessage;
}
