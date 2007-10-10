/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner;

import java.util.concurrent.TimeUnit;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.exceptions.TestFailedException;
import org.cubictest.export.exceptions.UserCancelledException;
import org.cubictest.export.runner.BaseTestRunner;
import org.cubictest.export.runner.RunnerWorkerThread.Operation;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.Browser;
import org.cubictest.exporters.selenium.runner.util.SeleniumWorkerThread;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
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
	SeleniumWorkerThread controller;
	Selenium selenium;
	ExtensionPoint targetExPoint;

	
	public TestRunner(Test test, ExtensionPoint targetExPoint, Display display, CubicTestProjectSettings settings) {
		super(display, settings, test);
		this.test = test;
		this.targetExPoint = targetExPoint;
	}


	public TestRunner(Test test, Selenium selenium, CubicTestProjectSettings settings) {
		super(null, settings, test);
		this.test = test;
		this.selenium = selenium;
	}
	
	public void run(IProgressMonitor monitor) {
		
		try {
			controller = new SeleniumWorkerThread();
			controller.setInitialUrlStartPoint(getInitialUrlStartPoint(test));
			controller.setBrowser(Browser.FIREFOX);
			controller.setDisplay(display);
			controller.setSelenium(selenium);
			controller.setSettings(settings);
			
			//start Selenium (browser and server), guard by timeout:
			controller.setOperation(Operation.START);
			
			int timeout = SeleniumUtils.getTimeout(settings);
			seleniumHolder = call(controller, timeout, TimeUnit.SECONDS);
			
			//ser monitor used to detect user cancel request:
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
			
			testWalker.convertTest(test, targetExPoint, seleniumHolder);

			if (monitor != null) {
				monitor.done();
			}

		}
		catch (UserCancelledException e) {
			//ok, user cancelled
		}
		catch (TestFailedException e) {
			throw e;
		}
		catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
	}

	

	/**
	 * Method for stopping Selenium. Can be invoked by a client class.
	 */
	public void stopSelenium() {
		try {
			if (controller != null) {
				controller.setOperation(Operation.STOP);
				call(controller, 20, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			ErrorHandler.rethrow(e);
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

}