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
package org.cubictest.exporters.selenium.launch;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.exceptions.UserCancelledException;
import org.cubictest.export.runner.RunnerStarter.Operation;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.launch.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.cubictest.exporters.selenium.runner.util.SeleniumStarter;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.selenium.Selenium;

public class TestRunner {

	private static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();
	private SeleniumHolder seleniumHolder;
	private SeleniumStarter seleniumStarter;
	private Selenium selenium;
	private Page targetPage;
	private int seleniumPort;
	public static final BrowserType DEFAULT_BROWSER = BrowserType.FIREFOX;
	private BrowserType browserType = DEFAULT_BROWSER;
	private IProgressMonitor monitor;
	private boolean reuseSelenium = false;
	private Test test;
	private boolean failOnAssertionFailure;
	private final int serverPort;
	private final int seleniumClientProxyPort;
	private CubicTestRemoteRunnerClient cubicTestRemoteRunnerClient;
	private SeleniumClientProxyServer seleniumClientProxyServer;
	private final Display display;

	public TestRunner(Test test, Display display, int seleniumPort, int serverPort,
			int seleniumClientProxyPort, BrowserType browserType) {
		this.test = test;
		this.display = display;
		this.seleniumPort = seleniumPort;
		this.serverPort = serverPort;
		this.seleniumClientProxyPort = seleniumClientProxyPort;
		this.browserType = browserType;
	}

	public void run(IProgressMonitor monitor) {
		this.monitor = monitor;

		try {
			if (seleniumHolder == null || !reuseSelenium) {
				startSelenium(monitor);
			}

			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(
					UrlStartPointConverter.class, PageElementConverter.class,
					ContextConverter.class, TransitionConverter.class,
					CustomTestStepConverter.class);

			if (monitor != null) {
				monitor.beginTask("Traversing the test model...",
						IProgressMonitor.UNKNOWN);
			}
			
			
			cubicTestRemoteRunnerClient = new CubicTestRemoteRunnerClient(serverPort);
			seleniumHolder.setCustomStepRunner(cubicTestRemoteRunnerClient);
			
			seleniumClientProxyServer = new SeleniumClientProxyServer(seleniumHolder, seleniumClientProxyPort);
			seleniumClientProxyServer.start();
			
			testWalker.convertTest(test, seleniumHolder, targetPage);

			if (monitor != null) {
				monitor.done();
			}

		} catch (Exception e) {
			if (monitor != null && monitor.isCanceled()) {
				throw new UserCancelledException("User cancelled");
			} else {
				ErrorHandler.rethrow(e);
			}
		}
	}

	private void startSelenium(final IProgressMonitor monitor)
			throws InterruptedException {
		seleniumStarter = new SeleniumStarter();
		seleniumStarter.setInitialUrlStartPoint(getInitialUrlStartPoint(test));
		seleniumStarter.setBrowser(browserType);
		seleniumStarter.setDisplay(display);
		seleniumStarter.setSelenium(selenium);
		seleniumStarter.setPort(seleniumPort);

		if (monitor != null) {
			Thread cancelHandler = new Thread() {
				@Override
				public void run() {
					try {
						while (seleniumStarter != null
								|| seleniumHolder.isSeleniumStarted()) {
							if (monitor.isCanceled()) {
								stopSelenium();
							}
							Thread.sleep(100);
						}
					} catch (Exception e) {
						Logger.warn("Exception in CancelHandler.", e);
					}
				}
			};
			cancelHandler.start();
		}

		// start Selenium (browser and server), guard by timeout:
		int timeout = 1000;
		try {
			seleniumStarter.setOperation(Operation.START);
			seleniumHolder = call(seleniumStarter, timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			ErrorHandler.rethrow("Unable to start "
					+ browserType.getDisplayName()
					+ ". Check that the browser is installed.\n\n"
					+ "Error message: " + e.toString(), e);
		}

		// monitor used to detect user cancel request:
		seleniumHolder.setMonitor(monitor);
		seleniumHolder.setFailOnAssertionFailure(failOnAssertionFailure);

		while (!seleniumHolder.isSeleniumStarted()) {
			// wait for selenium (server & test system) to start
			Thread.sleep(100);
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
			} else {
				ErrorHandler.rethrow(e);
			}
		} finally {
			if (seleniumHolder != null) {
				seleniumHolder.setSeleniumStarted(false);
			}
		}
	}

	/**
	 * Show the results of the test in the GUI.
	 * 
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

	/**
	 * Get the initial URL start point of the test (expands subtests).
	 */
	private UrlStartPoint getInitialUrlStartPoint(Test test) {
		if (test.getStartPoint() instanceof UrlStartPoint) {
			return (UrlStartPoint) test.getStartPoint();
		} else if (test.getStartPoint() instanceof ExtensionStartPoint) {
			// Get url start point recursively:
			return getInitialUrlStartPoint(((ExtensionStartPoint) test
					.getStartPoint()).getTest(true));
		} else if (test.getStartPoint() instanceof TestSuiteStartPoint) {
			// Get url start point of first sub-test:
			if (!(test.getFirstNodeAfterStartPoint() instanceof SubTest)) {
				ErrorHandler
						.logAndShowErrorDialogAndThrow("Test suites must contain at least one sub test after the test suite start point.\n\n"
								+ "To add a subtest, drag test from package explorer into the test suite editor.");
			}
			return getInitialUrlStartPoint(((SubTest) test
					.getFirstNodeAfterStartPoint()).getTest(true));
		}
		return null;
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

	protected static <T> T call(Callable<T> c, long timeout, TimeUnit timeUnit)
			throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<T> t = new FutureTask<T>(c);
		THREADPOOL.execute(t);
		return t.get(timeout, timeUnit);
	}

	public void cleanUp() {
		if(cubicTestRemoteRunnerClient != null){
			cubicTestRemoteRunnerClient.executeOnServer("stop");
		}
		if(seleniumClientProxyServer != null){
			seleniumClientProxyServer.shutdown();
		}
		stopSelenium();
		
	}

}
