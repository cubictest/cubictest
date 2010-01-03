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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.runner.RunnerStarter.Operation;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.SameVMCustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.SeleniumStarter;
import org.cubictest.model.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * Runner that can use an existing Selenium instance to run the tests.
 * 
 * @author Christian Schwarz
 */
public class TestRunner {

	protected static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();
	protected CubicTestProjectSettings settings;
	SeleniumHolder seleniumHolder;
	SeleniumStarter seleniumStarter;
	Selenium selenium;
	boolean reuseSelenium = false;
	private final SeleniumRunnerConfiguration config;

	
	public TestRunner(SeleniumRunnerConfiguration config, CubicTestProjectSettings settings) {
		this.settings = settings;
		this.config = config;
	}
	
	public void run(Test test) {
		try {
			if (seleniumHolder == null || !reuseSelenium) {
				startSeleniumAndOpenInitialUrlWithTimeoutGuard(test, 40);
			}
			
			seleniumHolder.setWorkingDir(config.getHtmlCaptureAndScreenshotsTargetDir());
			seleniumHolder.setUseNamespace(config.isSupportXHtmlNamespaces());
			seleniumHolder.setTakeScreenshots(config.isTakeScreenshots());
			seleniumHolder.setCaptureHtml(config.isCaptureHtml());

			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(UrlStartPointConverter.class, 
					PageElementConverter.class, ContextConverter.class, 
					TransitionConverter.class, SameVMCustomTestStepConverter.class);
			
			//walk the test!
			testWalker.convertTest(test, seleniumHolder, null);
			
		}
		catch (Exception e) {
			ErrorHandler.rethrow(e);
		}
	}


	/**
	 * Start selenium and opens initial URL, all guarded by a timeout.
	 */
	private void startSeleniumAndOpenInitialUrlWithTimeoutGuard(Test test, int timeoutSeconds)
			throws InterruptedException {
		
		seleniumStarter = new SeleniumStarter(config);
		seleniumStarter.setInitialUrlStartPoint(ExportUtils.getInitialUrlStartPoint(test));
		seleniumStarter.setSelenium(selenium);
		seleniumStarter.setStartNewSeleniumServer(config.shouldStartCubicSeleniumServer());
		seleniumStarter.setSettings(settings);

		//start Selenium (browser and server), guard by timeout:
		try {
			seleniumStarter.setOperation(Operation.START);
			seleniumHolder = call(seleniumStarter, timeoutSeconds, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			ErrorHandler.rethrow("Unable to start " + config.getBrowser().getDisplayName() + 
					" and open initial URL.\n\n" +
							"Check that\n" +
							"- The browser is installed (if in non-default dir, set it in PATH)\n" +
							"- The initial URL is correct\n" +
							"- There are no background (non-visible) browser processes hanging" +
							"\n\n"
					+ "Error message: " + e.toString(), e);
		}
		
		seleniumHolder.setFailOnAssertionFailure(true);

		while (!seleniumHolder.isSeleniumStarted()) {
			//wait for selenium (server & test system) to start
			Thread.sleep(100);
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

	public void setReuseSelenium(boolean reuseSelenium) {
		this.reuseSelenium = reuseSelenium;
	}

	/**
	 * Call a callable object, guarded by timeout.
	 */
	protected static <T> T call(Callable<T> c, long timeout, TimeUnit timeUnit)
	    throws InterruptedException, ExecutionException, TimeoutException {
	    FutureTask<T> t = new FutureTask<T>(c);
	    THREADPOOL.execute(t);
	    return t.get(timeout, timeUnit);
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
			ErrorHandler.rethrow(e);
		}
		finally {
			if (seleniumHolder != null) {
				seleniumHolder.setSeleniumStarted(false);
			}
		}
	}
	
}
