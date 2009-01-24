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

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.runner.RunnerStarter.Operation;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.launch.converters.LaunchCustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.SeleniumRunnerConfiguration;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.SeleniumStarter;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.selenium.Selenium;

public class TestRunner {

	private static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();
	private SeleniumHolder seleniumHolder;
	private SeleniumStarter seleniumStarter;
	private Selenium selenium;
	private Page targetPage;
	private IProgressMonitor monitor;
	private boolean reuseSelenium = false;
	private boolean failOnAssertionFailure;
	private CubicTestRemoteRunnerClient cubicTestRemoteRunnerClient;
	private SeleniumClientProxyServer seleniumClientProxyServer;
	private final RunnerParameters runnerParameters;
	SeleniumRunnerConfiguration config;
	

	public static class RunnerParameters {
		public Test test;
		public Display display;
		public int remoteRunnerClientListenerPort;
		public int seleniumClientProxyPort;
	}


	public TestRunner(RunnerParameters runnerParameters, SeleniumRunnerConfiguration config) {
		this.runnerParameters = runnerParameters;
		this.config = config;
	}


	public void run(IProgressMonitor monitor) {
		this.monitor = monitor;

		try {
			if (seleniumHolder == null || !reuseSelenium) {
				startSeleniumAndOpenInitialUrlWithTimeoutGuard(monitor, 40);
			}
			seleniumHolder.setWorkingDir(config.getWorkingDirName());
			seleniumHolder.setUseNamespace(config.isUseNamespace());
			seleniumHolder.setTakeScreenshots(config.isTakeScreenshots());
			seleniumHolder.setCaptureHtml(config.isCaptureHtml());
			
			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(
					UrlStartPointConverter.class, PageElementConverter.class,
					ContextConverter.class, TransitionConverter.class,
					LaunchCustomTestStepConverter.class);

			if (monitor != null) {
				monitor.beginTask("Traversing the test model...",
						IProgressMonitor.UNKNOWN);
			}
			
			cubicTestRemoteRunnerClient = new CubicTestRemoteRunnerClient(runnerParameters.remoteRunnerClientListenerPort);
			seleniumHolder.setCustomStepRunner(cubicTestRemoteRunnerClient);
			
			seleniumClientProxyServer = new SeleniumClientProxyServer(seleniumHolder, runnerParameters.seleniumClientProxyPort);
			seleniumClientProxyServer.start();
			
			//run the test!
			testWalker.convertTest(runnerParameters.test, seleniumHolder, targetPage);

			if (monitor != null) {
				monitor.done();
			}

		} catch (Exception e) {
			if (monitor != null && monitor.isCanceled()) {
				Logger.warn("User cancelled", e);
			} else {
				ErrorHandler.rethrow("Exception when running test", e);
			}
		}
	}

	
	/**
	 * Start selenium and opens initial URL, all guarded by a timeout.
	 */
	private void startSeleniumAndOpenInitialUrlWithTimeoutGuard(final IProgressMonitor monitor, int timeoutSeconds)
			throws InterruptedException {
		
		seleniumStarter = new SeleniumStarter(config);
		seleniumStarter.setInitialUrlStartPoint(ExportUtils.getInitialUrlStartPoint(runnerParameters.test));
		seleniumStarter.setDisplay(runnerParameters.display);
		seleniumStarter.setSelenium(selenium);
		seleniumStarter.setStartNewSeleniumServer(config.isServerAutoHostAndPort());
		seleniumStarter.setSettings(new CubicTestProjectSettings(runnerParameters.test.getProject())); 
		
		//start cancel handler, in case we want to cancel the Selenium startup or test run:
		if (monitor != null) {
			Thread cancelHandler = new Thread() {
				@Override
				public void run() {
					try {
						while (seleniumIsRunnningOrStarting()) {
							if (monitor.isCanceled()) {
								stopSeleniumWithTimeoutGuard(20);
							}
							Thread.sleep(100);
						}
					} catch (Exception e) {
						Logger.warn("Exception in CancelHandler.", e);
					}
				}

				private boolean seleniumIsRunnningOrStarting() {
					return seleniumStarter != null || (seleniumHolder != null && seleniumHolder.isSeleniumStarted());
				}
			};
			cancelHandler.start();
		}

		// start Selenium (browser and server), guard by timeout:
		try {
			seleniumStarter.setOperation(Operation.START);
			seleniumHolder = call(seleniumStarter, timeoutSeconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			ErrorHandler.rethrow("Unable to start " + config.getBrowser().getDisplayName() + 
					" and open initial URL.\n\n" +
							"Check that\n" +
							"- The browser is installed (if in non-default dir, set it in PATH)\n" +
							"- The initial URL is correct\n" +
							"- There are no background (non-visible) browser processes hanging" +
							"\n\n"
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
	 * Stop selenium, guarded by a timeout.
	 */
	public void stopSeleniumWithTimeoutGuard(int timeoutSeconds) {
		try {
			if (seleniumStarter != null) {
				seleniumStarter.setOperation(Operation.STOP);
				call(seleniumStarter, timeoutSeconds, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			if (monitor != null && monitor.isCanceled()) {
				//user has cancelled. fail more silently than other situations:
				Logger.warn("Exception when stopping selenium.", e);
			} else {
				ErrorHandler.rethrow(e);
			}
		} finally {
			seleniumStarter = null;
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
		try {
			if(cubicTestRemoteRunnerClient != null){
				cubicTestRemoteRunnerClient.executeOnServer("stop");
			}
		}
		catch (Exception e) {
			Logger.warn("Error when stopping Selenium", e);
		}
	
		try {
			if(seleniumClientProxyServer != null){
				seleniumClientProxyServer.shutdown();
			}
		}
		catch (Exception e) {
			Logger.warn("Error when stopping the Selenium Client Proxy Server", e);
		}
		
		try {
			stopSeleniumWithTimeoutGuard(20);
		}
		catch (Exception e) {
			Logger.warn("Error when stopping the Selenium Server", e);
		}
		
	}

}
