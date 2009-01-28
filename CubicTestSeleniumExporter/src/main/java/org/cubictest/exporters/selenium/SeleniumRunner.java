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
package org.cubictest.exporters.selenium;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.EmptyTestSuiteException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.exporters.selenium.common.SeleniumExporterProjectSettings;
import org.cubictest.exporters.selenium.runner.SeleniumRunnerConfiguration;
import org.cubictest.exporters.selenium.runner.TestRunner;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.openqa.selenium.server.RemoteControlConfiguration;


/**
 * Has static methods for running tests.
 * Can easily be invoked e.g. from a unit test.
 * 
 * @author Christian Schwarz
 */
public class SeleniumRunner 
{
	
    private static final String SEPERATOR = "------------------------------------------------------------------------";
	private static final String SMALL_SEPERATOR = "-----------------------------------------------";
	private static final String LOG_PREFIX = "[CubicTest Selenium Runner] ";

	private static final boolean REUSE_BROWSER_DEFAULT = true;
	private boolean reuseBrowser = REUSE_BROWSER_DEFAULT;
	private TestRunner testRunner;
	private SeleniumRunnerConfiguration config;
	private CubicTestProjectSettings settings;

	/**
	 * Create a new instance of the runner.
	 * Default settings:
	 * * Use browser set in test-project.properties.
	 * * Reuse browser instance between test files.
	 * * No capture of HTML or screenshots of failed tests.
	 * * No support for XHTML namespaces.
	 */
	public SeleniumRunner() {
		this.config = new SeleniumRunnerConfiguration();
		config.setMultiWindow(false);
		settings = new CubicTestProjectSettings(new File("."));
		config.setBrowser(SeleniumExporterProjectSettings.getPreferredBrowser(settings));
	}

	/**
	 * Run tests in specified directory and all subdirectories.
	 * @param directoryPath Path to directory to run tests in, relative to project root.
	 */
    @SuppressWarnings("unchecked")
	public void runTests(String directoryPath)
    {
    	if (StringUtils.isBlank(directoryPath)) {
    		throw new ExporterException("Please specify a path relative to the project root. E.g. \"/tests\"");
    	}
    	
    	if (directoryPath.equals("/")) {
    		directoryPath = ".";
    	}
    	else if (directoryPath.startsWith("/")) {
			//remove "/" as it should be relative to project root, not file system root.
			directoryPath = directoryPath.substring(1);
    	}

    	File dir = new File(directoryPath);
    	if (!dir.exists() || dir.isFile()) {
    		throw new ExporterException("The directory " + dir.getAbsolutePath() + " does not exist");
    	}
    	
		System.out.println(LOG_PREFIX + "Running all tests in folder " + dir.getAbsolutePath() + " and subfolders.");
    	
        Collection<File> files = FileUtils.listFiles(dir, new String[] {"aat", "ats"}, true);
        if (files.isEmpty()) {
        	System.out.println(LOG_PREFIX + "No .aat or .ats tests found in folder " + dir.getAbsolutePath());
        }
        else {
        	System.out.println(LOG_PREFIX + "Found " + files.size() + " tests.");
        }
        
        runTests(files);
    }
    
	/**
	 * Run specified test.
	 * @param testPath Path to test to run, relative to project root.
	 */
    @SuppressWarnings("unchecked")
	public void runTest(String testPath)
    {
    	if (StringUtils.isBlank(testPath)) {
    		throw new ExporterException("Please specify a path relative to the project root. E.g. \"/tests/test.aat\"");
    	}
    	
    	if (!testPath.endsWith(".aat") && !testPath.endsWith(".ats")) {
    		throw new ExporterException("File extension must be .aat or .ats");
    	}
    	
    	if (testPath.startsWith("/")) {
			//remove "/" as it should be relative to project root, not file system root.
    		testPath = testPath.substring(1);
    	}
    	
    	File file = new File(testPath);
    	
    	if (!file.exists()) {
    		throw new ExporterException("Test file not found: " + file.getAbsolutePath());
    	}
    	
    	Collection<File> files = new ArrayList<File>();
    	files.add(file);
    	
        runTests(files);
    }

	public void runTests(Collection<File> files) throws AssertionError {
        
        List<String> passedTests = new ArrayList<String>();
        List<String> failedTests = new ArrayList<String>();
        List<String> exceptionTests = new ArrayList<String>();
        List<String> notRunTests = new ArrayList<String>();
        
        for (File file : files) {
			notRunTests.add(file.getName());
		}

        boolean buildOk = true;
        
        System.out.println(LOG_PREFIX + "Keep browser open between test suite files: " + reuseBrowser);
        
        if (reuseBrowser && testRunner == null) {
        	//we start the browser just once for this instance
			testRunner = new TestRunner(config, null, null, settings, true);
			testRunner.setReuseSelenium(true);
			testRunner.setFailOnAssertionFailure(true);
        }
        
        for (File file : files) {
        	System.out.println(LOG_PREFIX + "Running test: " + file);
        	Test test = TestPersistance.loadFromFile(file, null);
        	System.out.println(LOG_PREFIX + "Test loaded: " + test.getName());

    		try {
    			notRunTests.remove(file.getName());
    			if (reuseBrowser) {
    				testRunner.setTest(test);
        			testRunner.run(null);
        			passedTests.add(file.getName());
    			}
    			else {
    				testRunner = new TestRunner(config, test, null, settings,true);
        			testRunner.setFailOnAssertionFailure(true);
        			testRunner.run(null);
        			passedTests.add(file.getName());
                	smallLogSeperator();
                	System.out.println(LOG_PREFIX + "Test run finished: " + file.getName() + ": " + testRunner.getResultMessage());
                	smallLogSeperator();
        			stopSelenium(testRunner);
        			Thread.sleep(800); //do not reopen firefox immediately
    			}
    		}
    		catch (EmptyTestSuiteException e) {
    			System.out.println(SEPERATOR);
    			System.err.println("Warning: Test suites should contain at least one test. " + 
						"To add a test, drag it from the package explorer into the test suite editor.");
    			throw new AssertionError("Test suite was empty: " + file.getName());
    		}
    		catch (ExporterException e) {
    			System.err.println(LOG_PREFIX + "Test failure detected. Stopping Selenium.");
    			stopSelenium(testRunner);
            	logSeperator();
    			System.err.println("Failure in test " + file.getName() + ": " + e.getMessage());
            	logSeperator();
            	System.out.println("Failure path: " + file.getName() + " --> " + testRunner.getCurrentBreadcrumbs());
            	if (!reuseBrowser) {
            		logSeperator();
	            	System.out.println(file.getName() + ": " + testRunner.getResultMessage());
            	}
    			failedTests.add(file.getName());
    			buildOk = false;
    			testRunner = null;
    			break;
    		}
    		catch (Throwable e) {
    			System.err.println(LOG_PREFIX + "Error detected during test run. Stopping Selenium.");
    			stopSelenium(testRunner);
    			testRunner = null;
    			System.err.println(e);
    			exceptionTests.add(file.getName());
    			buildOk = false;
    			break;
			}
        }
		if (!reuseBrowser) {
			if (!files.isEmpty() && testRunner != null) {
    			stopSelenium(testRunner);
			}
	    	logSeperator();
        	System.out.println("Test run finished: " + testRunner.getResultMessage());
			testRunner = null;
		}        
    	logSeperator();
        System.out.println("Tests passed: " + passedTests.toString());
        System.out.println("Tests failed: " + failedTests.toString());
        System.out.println("Threw exception: " + exceptionTests.toString());
        System.out.println("Tests not run: " + notRunTests.toString());

        if (!buildOk) {
        	throw new AssertionError("[CubicTest] There were test failures. See console/log output for failure details.");
        }
	}
    
	private static void logSeperator() {
		System.out.println(SEPERATOR);
	}

	private static void smallLogSeperator() {
		System.out.println(SMALL_SEPERATOR);
	}

	private static void stopSelenium(TestRunner testRunner) {
		try {
			((TestRunner) testRunner).stopSeleniumWithTimeoutGuard(20);
		}
		catch (Exception e) {
			System.err.println("Error stopping selenium.");
			e.printStackTrace();
		}
	}

	/**
	 * Set option to force new browser instance for each test.
	 */
	public void setUseNewBrowserInstanceForEachTest(boolean useNewBrowserInstanceForEachTest) {
		this.reuseBrowser = !useNewBrowserInstanceForEachTest;
	}
	
	
	public void setBrowser(BrowserType browserType) {
		config.setBrowser(browserType);
	}
	
	/**
	 * Set hostname (or IP address) and port of existing Selenium Server to use.
	 * This will prevent CubicTest from starting its own Selenium Server at localhost and random port.
	 */
	public void setUseExistingSeleniumServer(String seleniumServerHostname, int seleniumServerPort) {
		config.setUseExistingSeleniumServer(seleniumServerHostname, seleniumServerPort);
	}
	
	public void setCaptureHtml(boolean captureHtml) {
		config.setCaptureHtml(captureHtml);
	}

	public void setSupportXHtmlNamespaces(boolean useNamespace) {
		config.setSupportXHtmlNamespaces(useNamespace);
	}

	public void setHtmlCaptureAndScreenshotsTargetDir(String absolutePath) {
		config.setHtmlCaptureAndScreenshotsTargetDir(absolutePath);
	}

	public void setTakeScreenshots(boolean takeScreenshots) {
		config.setTakeScreenshots(takeScreenshots);
	}


	public void setRemoteControlConfiguration(RemoteControlConfiguration rcConfiguration) {
		config.setRemoteControlConfiguration(rcConfiguration);
	}


	public void setMultiWindow(boolean seleniumMultiWindow) {
		config.setMultiWindow(seleniumMultiWindow);
	}
	
}