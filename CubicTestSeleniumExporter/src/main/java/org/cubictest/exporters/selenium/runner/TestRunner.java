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
package org.cubictest.exporters.selenium.runner;

import java.util.concurrent.TimeUnit;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.exceptions.UserCancelledException;
import org.cubictest.export.runner.BaseTestRunner;
import org.cubictest.export.runner.RunnerStarter.Operation;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.cubictest.exporters.selenium.runner.util.SeleniumStarter;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
import org.cubictest.exporters.selenium.ui.SeleniumSettingsPage;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.selenium.Selenium;

/**
 * The runner that starts the Selenium servers and test system and starts traversal of the test model.
 * 
 * @author Christian Schwarz
 */
public class TestRunner extends BaseTestRunner {

	SeleniumHolder seleniumHolder;
	SeleniumStarter seleniumStarter;
	Selenium selenium;
	Page targetPage;
	static Integer port; 
	public static final BrowserType DEFAULT_BROWSER = BrowserType.FIREFOX;
	BrowserType browserType = DEFAULT_BROWSER;
	private IProgressMonitor monitor;

	/**
	 * Typically invoked by the CubicTest Selenium exporter Eclipse plugin.
	 */
	public TestRunner(Test test, Display display, CubicTestProjectSettings settings, BrowserType browserType) {
		super(display, settings, test);
		this.test = test;
		if (port == null) {
			port = ExportUtils.findAvailablePort();
		}
		this.browserType = browserType;
	}


	/**
	 * Typically invoked by the Maven plugin.
	 */
	public TestRunner(Test test, Selenium selenium, CubicTestProjectSettings settings) {
		super(null, settings, test);
		this.test = test;
		this.selenium = selenium;
		if (port == null) {
			port = ExportUtils.findAvailablePort();
		}
		this.browserType = BrowserType.fromId(settings.getString(SeleniumUtils.getPluginPropertyPrefix(), "defaultBrowserType", 
				DEFAULT_BROWSER.getId()));
	}
	
	public void run(IProgressMonitor monitor) {
		this.monitor = monitor;
		
		try {
			seleniumStarter = new SeleniumStarter();
			seleniumStarter.setInitialUrlStartPoint(getInitialUrlStartPoint(test));
			seleniumStarter.setBrowser(browserType);
			seleniumStarter.setDisplay(display);
			seleniumStarter.setSelenium(selenium);
			seleniumStarter.setSettings(settings);
			seleniumStarter.setPort(port);

			CancelHandler cancelHandler = new CancelHandler(monitor, this);
			cancelHandler.start();
			
			//start Selenium (browser and server), guard by timeout:
			int timeout = SeleniumUtils.getTimeout(settings) + 10;
			try {
				seleniumStarter.setOperation(Operation.START);
				seleniumHolder = call(seleniumStarter, timeout, TimeUnit.SECONDS);
			}
			catch (Exception e) {
				ErrorHandler.rethrow("Unable to start " + browserType.getDisplayName() + 
						". Check that the browser is installed.\n\n" + 
						"Error message: " + e.toString(), e);
			}
			
			//monitor used to detect user cancel request:
			seleniumHolder.setMonitor(monitor);
			seleniumHolder.setFailOnAssertionFailure(failOnAssertionFailure);
			

			while (!seleniumHolder.isSeleniumStarted()) {
				//wait for selenium (server & test system) to start
				Thread.sleep(100);
			}
			
			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(UrlStartPointConverter.class, 
					PageElementConverter.class, ContextConverter.class, 
					TransitionConverter.class, CustomTestStepConverter.class);
			
			if (monitor != null) {
				monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
			}
			
			testWalker.convertTest(test, seleniumHolder, targetPage);

			if (monitor != null) {
				monitor.done();
			}

		}
		catch (Exception e) {
			if (monitor != null && monitor.isCanceled()) {
				throw new UserCancelledException("User cancelled");
			}
			else {
				ErrorHandler.logAndRethrow(e);
			}
		}
	}

	

	/**
	 * Method for stopping Selenium. Can be invoked by a client class.
	 */
	public void stopSelenium() {
		try {
			if (seleniumStarter != null) {
				seleniumStarter.setOperation(Operation.STOP);
				call(seleniumStarter, 20, TimeUnit.SECONDS);
			}
			seleniumStarter = null;
		} catch (Exception e) {
			if (monitor != null && monitor.isCanceled()) {
				Logger.warn("Exception when stopping selenium.", e);
			}
			else {
				ErrorHandler.rethrow(e);
			}
		}
		finally {
			if (seleniumHolder != null) {
				seleniumHolder.setSeleniumStarted(false);
			}
		}
	}

	/**
	 * Show the results of the test in the GUI.
	 * @return
	 */
	public String getResultMessage() {
		if (seleniumHolder != null) {
			return seleniumHolder.getResults();
		}
		return "";
	}
	
	
	/**
	 * Get the initial URL start point of the test (expands subtests).
	 */
	private UrlStartPoint getInitialUrlStartPoint(Test test) {
		if (test.getStartPoint() instanceof UrlStartPoint) {
			return (UrlStartPoint) test.getStartPoint();
		}
		else if (test.getStartPoint() instanceof ExtensionStartPoint) {
			//Get url start point recursively:
			return getInitialUrlStartPoint(((ExtensionStartPoint) test.getStartPoint()).getTest(true));
		}
		else if (test.getStartPoint() instanceof TestSuiteStartPoint) {
			//Get url start point of first sub-test:
			if (!(test.getFirstNodeAfterStartPoint() instanceof SubTest)) {
				ErrorHandler.logAndShowErrorDialogAndThrow("Test suites must contain at least one sub test after the test suite start point.\n\n" + 
						"To add a subtest, drag test from package explorer into the test suite editor.");
			}
			return getInitialUrlStartPoint(((SubTest) test.getFirstNodeAfterStartPoint()).getTest(true));
		}
		return null;
	}


	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}


	public void setTargetPage(Page targetPage) {
		this.targetPage = targetPage;
	}

}
