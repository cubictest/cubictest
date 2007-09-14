/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.exporters.watir.utils.UserCancelledException;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.AbstractContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;


/**
 * Monitors a Watir process and updates the GUI 
 * @author Christian Schwarz
 *
 */
public class WatirMonitor {

	private List<PageElement> elementsAsserted = new ArrayList<PageElement>();
	private List<TestPartStatus> results = new ArrayList<TestPartStatus>();
	private IProgressMonitor monitor;
	private final Display display;
	private StepList stepList;
	
	
	public WatirMonitor(StepList stepList, Display display) {
		this.stepList = stepList;
		this.display = display;
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
	
	public String getResultInfo() {
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

	
	private void handleUserCancel() {
		if (monitor != null && monitor.isCanceled()) {
			throw new UserCancelledException("Operation cancelled");
		}
	}



	public void handle(String line) {
		if(line.startsWith(StepList.PASS)) {
			PageElement pe = stepList.getPageElement(line.substring(line.indexOf(StepList.PASS) + StepList.PASS.length()));
			addResult(pe, TestPartStatus.PASS);
		}
		else if(line.startsWith(StepList.FAIL)) {
			PageElement pe = stepList.getPageElement(line.substring(line.indexOf(StepList.FAIL) + StepList.FAIL.length()));
			addResult(pe, TestPartStatus.FAIL);
		}
		else if(line.startsWith(StepList.EXCEPTION)) {
			throw new ExporterException("Exception when running test: " + 
					line.substring(line.indexOf(StepList.EXCEPTION) + StepList.EXCEPTION.length()));
		}
	}
	
}
