/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.export.holders;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
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
	public void updateStatus(SubTest st, boolean hadException, ConnectionPoint targetConnectionPoint) {
		final boolean hadEx = hadException;
		final SubTest subtest = st;
		final ConnectionPoint targetConPoint = targetConnectionPoint;
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if(subtest != null)
						subtest.updateStatus(hadEx, targetConPoint);
				}
			});
		}
	}

	@Override
	public void updateStatus(final ICustomTestStepHolder ctsh, final TestPartStatus newStatus) {
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if(ctsh != null)
						ctsh.setStatus(newStatus);
				}
			});
		}
	}
	
	public String getResults() {
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
			else if (results.get(i).equals(TestPartStatus.EXCEPTION) ||
					results.get(i).equals(TestPartStatus.FAIL)) {
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

	public Display getDisplay() {
		return display;
	}
	
	public void resetStatus(final PropertyAwareObject object) {
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if(object != null)
						object.resetStatus();
				}
			});
		}
	}

}
