/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.holders;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.export.exceptions.UserCancelledException;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.AbstractContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Default base class for runner result holders.
 * 
 * @author Christian Schwarz
 */
public abstract class RunnerResultHolder extends ContextHolder {

	protected IProgressMonitor monitor;
	protected List<PageElement> elementsAsserted = new ArrayList<PageElement>();
	protected List<TestPartStatus> results = new ArrayList<TestPartStatus>();
	protected final Display display;
	protected CubicTestProjectSettings settings;
	private boolean failOnAssertionFailure;

	
	public RunnerResultHolder(Display display, CubicTestProjectSettings settings) {
		this.display = display;
		this.settings = settings;
	}

	/**
	 * Registers result and shows feedback in GUI. 
	 * @param element
	 * @param result
	 * @param isNot
	 */
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

	protected void handleAssertionFailure(PageElement element) {
		String childs = "";
		if (element instanceof AbstractContext) {
			AbstractContext context = (AbstractContext) element;
			childs = "\n\nRequired child elements of context (all must be present):\n" + context.getRootElements().toString();
		}
		if (failOnAssertionFailure) {
			throw new AssertionFailedException("Page element assertion failed: " + element.toString() + childs);
		}
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
	
	public void handleUserCancel() {
		if (monitor != null && monitor.isCanceled()) {
			throw new UserCancelledException("Operation cancelled");
		}
	}
	
	public void updateStatus(SubTest subtest, boolean hadException) {
		final boolean hadEx = hadException;
		final SubTest subTest = subtest;
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if(subTest != null)
						subTest.updateStatus(hadEx);
				}
			});
		}
	}
	
	public String getResults() {
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
		String res =  pass + " steps passed, " + failed + " steps failed";
		if (monitor != null && !monitor.isCanceled()) {
			res += getTestRunOkInfoAdditions();
		}
		return res;
	}

	protected String getTestRunOkInfoAdditions() {
		return "\n\nPress OK to close test browser.";
	}
	
	
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	public CubicTestProjectSettings getSettings() {
		return settings;
	}
	
	public void setFailOnAssertionFailure(boolean failOnAssertionFailure) {
		this.failOnAssertionFailure = failOnAssertionFailure;
	}

	public boolean shouldFailOnAssertionFailure() {
		return failOnAssertionFailure;
	}

}
