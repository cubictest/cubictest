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
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.EmptyTestSuiteException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.TestRunner;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;


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


	/**
	 * Run tests in specified directory and all subdirectories.
	 * @param dirString Path to directory to run tests in, relative to project root.
	 */
    @SuppressWarnings("unchecked")
	public static void runTests(String dirString)
    {
    	if (dirString.startsWith("/")) {
    		dirString = "." + dirString;
    	}
    	File dir = new File(dirString);
    	
		System.out.println(LOG_PREFIX + " Running all tests in folder " + dir.getAbsolutePath() + " and subfolders.");
    	
        CubicTestProjectSettings settings = new CubicTestProjectSettings(new File("."));

        Collection<File> files = FileUtils.listFiles(dir, new String[] {"aat", "ats"}, true);
        List<String> passedTests = new ArrayList<String>();
        List<String> failedTests = new ArrayList<String>();
        List<String> exceptionTests = new ArrayList<String>();
        List<String> notRunTests = new ArrayList<String>();
        
        for (File file : files) {
			notRunTests.add(file.getName());
		}

        boolean buildOk = true;
        
        TestRunner testRunner = null;
        boolean useFreshBrowser = settings.getBoolean(SeleniumUtils.getPluginPropertyPrefix(), "useNewBrowserInstanceForEachTestSuiteFile", false);
        System.out.println(LOG_PREFIX + " Use new browser instance for each test suite file: " + useFreshBrowser);
        boolean reuseBrowser = !useFreshBrowser;
        if (reuseBrowser) {
			testRunner = new TestRunner(null, null, settings, true);
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
    				testRunner = new TestRunner(test, null, settings,true);
        			testRunner.setFailOnAssertionFailure(true);
        			testRunner.run(null);
        			passedTests.add(file.getName());
                	smallLogSeperator();
                	System.out.println("Test run finished: " + file.getName() + ": " + testRunner.getResultMessage());
                	smallLogSeperator();
        			stopSelenium(testRunner);
        			Thread.sleep(800); //do not reopen firefox immediately
    			}
    		}
    		catch (EmptyTestSuiteException e) {
    			System.out.println(SEPERATOR);
    			System.err.println("Warning: Test suite was empty: " + file.getName());
    			System.err.println("Warning: Test suites should contain at least one test. " + 
						"To add a test, drag it from the package explorer into the test suite editor.");
    			return;
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
    			break;
    		}
    		catch (Throwable e) {
    			System.err.println(LOG_PREFIX + "Error detected during test run. Stopping Selenium.");
    			stopSelenium(testRunner);
    			System.err.println(e);
    			exceptionTests.add(file.getName());
    			buildOk = false;
    			break;
			}
        }
		if (reuseBrowser) {
			if (!files.isEmpty() && testRunner != null) {
    			stopSelenium(testRunner);
			}
	    	logSeperator();
        	System.out.println("Test run finished: " + testRunner.getResultMessage());
		}        
    	logSeperator();
        System.out.println("Tests passed: " + passedTests.toString());
        System.out.println("Tests failed: " + failedTests.toString());
        System.out.println("Threw exception: " + exceptionTests.toString());
        System.out.println("Tests not run: " + notRunTests.toString());

        if (!buildOk) {
        	throw new AssertionError("[CubicTest] There were test failures.");
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
}