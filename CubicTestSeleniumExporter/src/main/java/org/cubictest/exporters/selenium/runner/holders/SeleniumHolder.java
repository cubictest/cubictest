/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.holders;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.holders.RunnerResultHolder;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.selenium.Selenium;

/**
 * Holder that has reference to the Selenium test system (for running Selenium commands).
 * Also holds the results of the test and the current contexts (@see ContextHolder).
 *  
 * @author Christian Schwarz
 */
public class SeleniumHolder extends RunnerResultHolder {

	public static final String HTML_AND_SCREENSHOTS_FOLDER_NAME = "html and screenshots";
	private CubicTestLocalRunner selenium;
	private boolean seleniumStarted;
	private UrlStartPoint handledUrlStartPoint;
	private CubicTestRemoteRunnerClient customStepRunner;
	private String workingDirName;
	private String timestampFolder = new SimpleDateFormat("yyyy-MM-dd HHmm").format(new Date());
	private boolean takeScreenshots;
	private boolean captureHtml;
	private int nextPageElementTimeout;
	
	
	/**
	 * Use pre-configured and started Selenium instance from client e.g. the CubicRecorder
	 * @param selenium the Selenium to use.
	 * @param display the display for showing results.
	 * @param settings settings for the project.
	 */
	public SeleniumHolder(Selenium selenium, Display display, CubicTestProjectSettings settings) {
		super(display, settings);
		this.selenium = new CubicTestLocalRunner(selenium);
		this.nextPageElementTimeout = SeleniumUtils.getTimeout(settings);
	}
	
	public SeleniumHolder(String seleniumServerHostname, int seleniumServerPort, String browser, String initialUrl, Display display, CubicTestProjectSettings settings) {
		super(display, settings);
		if (seleniumServerPort < 80) {
			throw new ExporterException("Invalid port");
		}
		if(isBlank(seleniumServerHostname)) {
			seleniumServerHostname = "localhost";
		}
		this.selenium = new CubicTestLocalRunner(seleniumServerHostname, seleniumServerPort, browser, initialUrl);
		this.nextPageElementTimeout = SeleniumUtils.getTimeout(settings);
	}
	
	@Override
	protected void handleAssertionFailure(PropertyAwareObject element) {
		
		if (workingDirName != null && (takeScreenshots || captureHtml)) {
			String baseTargetFolder = workingDirName +  File.separator + HTML_AND_SCREENSHOTS_FOLDER_NAME;
			new File(baseTargetFolder).mkdir();
			String innerFolder = baseTargetFolder + File.separator + timestampFolder;
			new File(innerFolder).mkdir();
			innerFolder = innerFolder + File.separator;

			if (captureHtml) {
				try{
					String bodyText = selenium.execute("getHtmlSource")[0];
					SeleniumUtils.writeTextToFile(innerFolder, element.getName(), bodyText, "html");
				}
				catch (Throwable e) {
					Logger.warn("Unable to capture HTML of failing test", e);
				}
			}
			
			if (takeScreenshots) {
				try {
					selenium.execute("windowFocus");
					Thread.sleep(100);
					selenium.execute("captureScreenshot", innerFolder + element.getName() + ".png");
				}
				catch (Throwable e) {
					Logger.warn("Unable to capture screenshot of failing test", e);
				}
			}
			
			if (captureHtml || takeScreenshots) {
				Logger.info("HTML capture and/or screenshots of failed test saved to folder \"" + HTML_AND_SCREENSHOTS_FOLDER_NAME + "\"");
			}
		}

		super.handleAssertionFailure(element);
	}
	
	public boolean isSeleniumStarted() {
		return seleniumStarted;
	}

	public void setSeleniumStarted(boolean seleniumStarted) {
		this.seleniumStarted = seleniumStarted;
	}


	public void setHandledUrlStartPoint(UrlStartPoint initialUrlStartPoint) {
		this.handledUrlStartPoint = initialUrlStartPoint;
	}

	public UrlStartPoint getHandledUrlStartPoint() {
		return handledUrlStartPoint;
	}

	public void setCustomStepRunner(
			CubicTestRemoteRunnerClient customStepRunner) {
				this.customStepRunner = customStepRunner;
	}
	
	public CubicTestRemoteRunnerClient getCustomStepRunner() {
		return customStepRunner;
	}

	public void setWorkingDir(String workingDirName) {
		this.workingDirName = workingDirName;
	}

	public void setTakeScreenshots(boolean takeScreenshots) {
		this.takeScreenshots = takeScreenshots;
	}

	public void setCaptureHtml(boolean captureHtml) {
		this.captureHtml = captureHtml;
	}

	public void setTestName(String testName) {
		//not in use
	}

	/** Set next timeout in seconds */
	public void setNextPageElementTimeout(int nextPageElementTimeout) {
		this.nextPageElementTimeout = nextPageElementTimeout;
	}
	
	/** Get next timeout in seconds */
	public int getNextPageElementTimeout() {
		return nextPageElementTimeout;
	}

	public CubicTestLocalRunner getSelenium() {
		return selenium;
	}

}
