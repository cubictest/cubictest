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

import java.io.File;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.holders.RunnerResultHolder;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.context.Frame;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.selenium.Selenium;

/**
 * Holder that has reference to the Selenium test system (for running Selenium commands).
 * Also holds the results of the test and the current contexts (@see ContextHolder).
 *  
 * @author Christian Schwarz
 */
public class SeleniumHolder extends RunnerResultHolder {

	private CubicTestLocalRunner selenium;
	private boolean seleniumStarted;
	private UrlStartPoint handledUrlStartPoint;
	private CubicTestRemoteRunnerClient customStepRunner;
	private String workingDirName;
	
	
	public SeleniumHolder(Selenium selenium, Display display, CubicTestProjectSettings settings) {
		super(display, settings);
		//use Selenium from client e.g. the CubicRecorder
		this.selenium = new CubicTestLocalRunner(selenium);
	}
	
	public SeleniumHolder(String host, int port, String browser, String initialUrl, Display display, CubicTestProjectSettings settings) {
		super(display, settings);
		if (port < 80) {
			throw new ExporterException("Invalid port");
		}
		if(host == null)
			host = "localhost";
		this.selenium = new CubicTestLocalRunner(host, port, browser, initialUrl);
	}
	
	public SeleniumHolder(int port, String browser, String initialUrl, Display display, CubicTestProjectSettings settings) {
		this("localhost",port,browser, initialUrl, display, settings);
	}
	
	public CubicTestLocalRunner getSelenium() {
		
		return selenium;
	}

	@Override
	protected void handleAssertionFailure(PageElement element) {
		if (workingDirName != null) {
			try{
				String bodyText = selenium.execute("getHtmlSource")[0];
				SeleniumUtils.writeTextToFile(workingDirName, element.getText(), bodyText);
			}
			catch (Exception e) {
				Logger.warn("Unable to capture HTML of failing test", e);
			}
			
			try {
				selenium.execute("windowFocus");
				selenium.execute("captureScreenshot",workingDirName + File.separator + element.getText() + "_" + System.currentTimeMillis() + ".png");
				//SeleniumUtils.takeAScreenShotOfTheApp(workingDirName,element.getText());
			}
			catch (Exception e) {
				Logger.warn("Unable to capture screenshot of failing test", e);
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

}
