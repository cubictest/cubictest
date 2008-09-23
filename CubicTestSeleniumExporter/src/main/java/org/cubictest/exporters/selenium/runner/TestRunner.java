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
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.exceptions.UserCancelledException;
import org.cubictest.export.runner.BaseTestRunner;
import org.cubictest.export.runner.RunnerStarter.Operation;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.exporters.selenium.common.SeleniumExporterProjectSettings;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.SameVMCustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.UnsupportedCustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.SeleniumStarter;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
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
	BrowserType browserType = BrowserType.FIREFOX; //init
	private IProgressMonitor monitor;
	boolean reuseSelenium = false;
	private boolean runingInSameVMAsCustomFiles = false;
	

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
	public TestRunner(Test test, Selenium selenium, CubicTestProjectSettings settings, boolean runingInSameVMAsCustomFiles) {
		super(null, settings, test);
		this.test = test;
		this.selenium = selenium;
		this.runingInSameVMAsCustomFiles  = runingInSameVMAsCustomFiles;
		if (port == null) {
			port = ExportUtils.findAvailablePort();
		}
		this.browserType = SeleniumExporterProjectSettings.getPreferredBrowser(settings);
	}
	
	public void run(IProgressMonitor monitor) {
		this.monitor = monitor;
		
		try {
			if (seleniumHolder == null || !reuseSelenium) {
				startSeleniumAndOpenInitialUrlWithTimeoutGuard(monitor, 40);
			}
			Class<? extends ICustomTestStepConverter<SeleniumHolder>> ctsc = null;
			if(runingInSameVMAsCustomFiles)
				ctsc = SameVMCustomTestStepConverter.class;
			else 
				ctsc = UnsupportedCustomTestStepConverter.class;
				
			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(UrlStartPointConverter.class, 
					PageElementConverter.class, ContextConverter.class, 
					TransitionConverter.class, ctsc);
			
			if (monitor != null) {
				monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
			}
			
			//run the test!
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
				ErrorHandler.rethrow(e);
			}
		}
	}


	/**
	 * Start selenium and opens initial URL, all guarded by a timeout.
	 */
	private void startSeleniumAndOpenInitialUrlWithTimeoutGuard(final IProgressMonitor monitor, int timeoutSeconds)
			throws InterruptedException {
		seleniumStarter = new SeleniumStarter();
		seleniumStarter.setInitialUrlStartPoint(ExportUtils.getInitialUrlStartPoint(test));
		seleniumStarter.setBrowser(browserType);
		seleniumStarter.setDisplay(display);
		seleniumStarter.setSelenium(selenium);
		seleniumStarter.setSettings(settings);
		seleniumStarter.setPort(port);

		if (monitor != null) {
			CancelHandler cancelHandler = new CancelHandler(monitor, this);
			cancelHandler.start();
		}
		
		//start Selenium (browser and server), guard by timeout:
		try {
			seleniumStarter.setOperation(Operation.START);
			seleniumHolder = call(seleniumStarter, timeoutSeconds, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			ErrorHandler.rethrow("Unable to start " + browserType.getDisplayName() + 
					" and open initial URL.\n\n" +
							"Check that\n" +
							"- The browser is installed (if in non-default dir, set it in PATH)\n" +
							"- The initial URL is correct\n" +
							"- There are no background (non-visible) browser processes hanging" +
							"\n\n"
					+ "Error message: " + e.toString(), e);
		}
		
		//monitor used to detect user cancel request:
		seleniumHolder.setMonitor(monitor);
		seleniumHolder.setFailOnAssertionFailure(failOnAssertionFailure);
		

		while (!seleniumHolder.isSeleniumStarted()) {
			//wait for selenium (server & test system) to start
			Thread.sleep(100);
		}
	}

	

	/**
	 * Stop selenium, guarded by a timeout.
	 */
	public void stopSeleniumWithTimeoutGuard(int timeoutSeconds) {
		try {
			if (seleniumStarter != null) {
				seleniumStarter.setOperation(Operation.STOP);
				call(seleniumStarter, timeoutSeconds, TimeUnit.SECONDS);
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
	
	public String getCurrentBreadcrumbs() {
		return seleniumHolder.getCurrentBreadcrumbs();
	}
	


	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}


	public void setTargetPage(Page targetPage) {
		this.targetPage = targetPage;
	}


	public void setReuseSelenium(boolean reuseSelenium) {
		this.reuseSelenium = reuseSelenium;
	}

}
