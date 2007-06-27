/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.holders;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.util.UserCancelledException;
import org.cubictest.exporters.selenium.utils.ContextHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.context.AbstractContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Holder that has reference to the Selenium test system (for running Selenium commands).
 * Also holds the results of the test and the current contexts (@see ContextHolder).
 * Handles user cancel of the test run.
 *  
 * @author Christian Schwarz
 */
public class SeleniumHolder extends ContextHolder {

	private Selenium selenium;
	private List<PageElement> elementsAsserted = new ArrayList<PageElement>();
	private List<TestPartStatus> results = new ArrayList<TestPartStatus>();
	private boolean seleniumStarted;
	private IProgressMonitor monitor;
	private UrlStartPoint handledUrlStartPoint;
	private final Display display;
	private boolean failOnAssertionFailure;
	private CubicTestProjectSettings settings;
	
	public SeleniumHolder(Selenium selenium, Display display, CubicTestProjectSettings settings) {
		this.settings = settings;
		//use Selenium from client e.g. the CubicRecorder
		this.selenium = selenium;
		this.display = display;
	}
	
	public SeleniumHolder(int port, String browser, String initialUrl, Display display, CubicTestProjectSettings settings) {
		this.settings = settings;
		this.display = display;
		if (port < 80) {
			throw new ExporterException("Invalid port");
		}
		selenium = new DefaultSelenium("localhost", port, browser, initialUrl);
	}
	
	public Selenium getSelenium() {
		return selenium;
	}

	public void addResult(PageElement element, TestPartStatus result, boolean isNot) {
		if (isNot) {
			//negate result
			if (result.equals(TestPartStatus.PASS)) {
				result = TestPartStatus.FAIL;
			}
			else if (result.equals(TestPartStatus.FAIL)) {
				result = TestPartStatus.PASS;
			}
		}
		addResult(element, result);

		if (result.equals(TestPartStatus.FAIL)) {
			handleAssertionFailure(element);
		}
		
	}

	private void handleAssertionFailure(PageElement element) {
		String childs = "";
		if (element instanceof AbstractContext) {
			AbstractContext context = (AbstractContext) element;
			childs = "\n\nRequired child elements of context (all must be present):\n" + context.getElements().toString();
		}
		throw new AssertionFailedException("Page element assertion failed: " + element.toString() + childs);
	}
	
	public void addResult(final PageElement element, TestPartStatus result) {
		handleUserCancel();
		elementsAsserted.add(element);
		results.add(result);

		//show result immediately in the GUI:
		final TestPartStatus finalResult = result;
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if(element != null)
						element.setStatus(finalResult);
				}
			});
		}
		if (result.equals(TestPartStatus.FAIL)) {
			handleAssertionFailure(element);
		}
	}
	
	@Override
	public void updateStatus(SubTest theSubTest, boolean hadException) {
		final boolean hadEx = hadException;
		final SubTest subTest = theSubTest;
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if(subTest != null)
						subTest.updateStatus(hadEx);
				}
			});
		}
	}
	
	public String showResults() {
		handleUserCancel();
		int pass = 0;
		int failed = 0;
		int i = 0;
		for (PageElement element : elementsAsserted) {
			if (element != null) {
				element.setStatus(results.get(i));
			}
			if (results.get(i).equals(TestPartStatus.PASS)) {
				pass++;
			}
			else {
				failed++;
			}
			i++;
		}
		return pass + " steps passed, " + failed + " steps failed.";
	}

	public boolean isSeleniumStarted() {
		return seleniumStarted;
	}

	public void setSeleniumStarted(boolean seleniumStarted) {
		this.seleniumStarted = seleniumStarted;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	private void handleUserCancel() {
		if (monitor != null && monitor.isCanceled()) {
			throw new UserCancelledException("Operation cancelled");
		}
	}

	public void setHandledUrlStartPoint(UrlStartPoint initialUrlStartPoint) {
		this.handledUrlStartPoint = initialUrlStartPoint;
	}

	public UrlStartPoint getHandledUrlStartPoint() {
		return handledUrlStartPoint;
	}

	public void setFailOnAssertionFailure(boolean failOnAssertionFailure) {
		this.failOnAssertionFailure = failOnAssertionFailure;
	}

	public boolean shouldFailOnAssertionFailure() {
		return failOnAssertionFailure;
	}

	public CubicTestProjectSettings getSettings() {
		return settings;
	}

}
