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
package org.cubictest.exporters.selenium.ui;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.ITestRunner;
import org.cubictest.export.ui.BaseRunnerAction;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.TestRunner;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.thoughtworks.selenium.Selenium;

/**
 * Action for running a test using the Selenium runner.
 * 
 * @author Christian Schwarz
 */
public class RunSeleniumRunnerAction extends BaseRunnerAction {

	public static final String SELENIUM_RUNNER_BROWSER_TYPE = "SeleniumRunnerBrowserType";
	public static final String SELENIUM_RUNNER_REMEMBER_SETTINGS = "SeleniumRunnerRememberSettings";
	boolean stopSeleniumWhenFinished = true;
	Selenium selenium;
	private String customCompletedMessage;
	private boolean showCompletedMessageInStatusLine;
	private Test preSelectedTest;
	private Page targetPage;


	@Override
	public Shell getShell() {
		return SeleniumExporterPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}


	@Override
	public ITestRunner getTestRunner(Test test, Display display, CubicTestProjectSettings settings) {
		TestRunner runner = null;
		Shell shell = SeleniumExporterPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		BrowserType browserType = getSeleniumBrowserType(settings);
		
		SeleniumRunnerWizard wizard = new SeleniumRunnerWizard(browserType);
		WizardDialog wizDialog = new WizardDialog(shell, wizard);
		
		boolean ask = !usePreCreatedSeleniumInstance();
		try {
			String remember = SeleniumExporterPlugin.getDefault().getDialogSettings().get(SELENIUM_RUNNER_REMEMBER_SETTINGS);
			if ("true".equals(remember)) {
				ask = false;
			}
		}
		catch (Exception ignore) {
		}

		int returnCode = WizardDialog.OK;
		if (ask) {
			returnCode = wizDialog.open();
		}
		
		if(returnCode != WizardDialog.CANCEL){
			if (ask) {
				browserType = wizard.getBrowserType();
			}
			else {
				browserType = BrowserType.FIREFOX; //recorder
			}
			runner = new TestRunner(test, display, settings, browserType);
			if (usePreCreatedSeleniumInstance()) {
				runner.setSelenium(selenium);
			}
			runner.setTargetPage(targetPage);
		}
		return runner;
	}


	private boolean usePreCreatedSeleniumInstance() {
		return selenium != null;
	}


	@Override
	protected void finalCleanUp() {
		if (stopSeleniumWhenFinished && testRunner != null) {
			((TestRunner) testRunner).stopSelenium();
		}
	}

	
	@Override
	protected Test getTest() {
		Test test = null;
		if (preSelectedTest != null) {
			test = preSelectedTest;
		}
		else {
			test = testEditor.getTest();
		}
		return test;
	}
	
	
	@Override
	protected void showCompletedMessage(Shell shell, String result) {
		String msg = "Test run finished. " + result;
		if (StringUtils.isNotBlank(customCompletedMessage)) {
			//use custom message instead
			msg = StringUtils.replace(customCompletedMessage, "$result", result); 
		}

		if (showCompletedMessageInStatusLine) {
			UserInfo.setStatusLine(msg);
		}
		else {
			MessageDialog.openInformation(shell, "CubicTest Selenium Exporter", msg);
		}
	}


	
	public BrowserType getSeleniumBrowserType(CubicTestProjectSettings settings) {
		BrowserType browserType = null;
		try {
			int storedBrowserTypeIndex = SeleniumExporterPlugin.getDefault().getDialogSettings().getInt(SELENIUM_RUNNER_BROWSER_TYPE);
			if (storedBrowserTypeIndex < 0 || storedBrowserTypeIndex > BrowserType.values().length - 1) {
				storedBrowserTypeIndex = 0;
			}
			browserType = BrowserType.values()[storedBrowserTypeIndex];
		} 
		catch(NumberFormatException nfe) {
			try {
				browserType = BrowserType.fromId(settings.getString(SeleniumUtils.getPluginPropertyPrefix(), "defaultBrowserType",
						TestRunner.DEFAULT_BROWSER.getId()));
			} catch (Exception ignore) {
			}
		}
		return browserType;
	}
	

	public void setStopSeleniumWhenFinished(boolean stopSeleniumWhenFinished) {
		this.stopSeleniumWhenFinished = stopSeleniumWhenFinished;
	}

	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}

	public void setPreSelectedTest(Test test) {
		this.preSelectedTest = test;
	}

	public void setCustomCompletedMessage(String customCompletedMessage) {
		this.customCompletedMessage = customCompletedMessage;
	}
	
	public void setShowCompletedMessageInStatusLine(boolean b) {
		this.showCompletedMessageInStatusLine = b;
	}


	public void setTargetPage(Page targetPage) {
		this.targetPage = targetPage;
	}
}
