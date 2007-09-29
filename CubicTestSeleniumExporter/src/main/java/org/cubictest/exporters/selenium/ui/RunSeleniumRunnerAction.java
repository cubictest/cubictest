/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.ITestRunner;
import org.cubictest.export.ui.BaseRunnerAction;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.TestRunner;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.thoughtworks.selenium.Selenium;

/**
 * Action for running a test using the Selenium runner.
 * 
 * @author Christian Schwarz
 */
public class RunSeleniumRunnerAction extends BaseRunnerAction  {

	boolean stopSeleniumWhenFinished = true;
	Selenium selenium;
	private ExtensionPoint targetExPoint;
	private String customCompletedMessage;
	private boolean showCompletedMessageInStatusLine;
	private Test preSelectedTest;


	@Override
	public Shell getShell() {
		return SeleniumExporterPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}


	@Override
	public ITestRunner getTestRunner(Test test, Display display, CubicTestProjectSettings settings) {
		TestRunner runner = new TestRunner(test, targetExPoint, display, settings);
		if (selenium != null) {
			runner.setSelenium(selenium);
		}
		return runner;
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


	

	public void setStopSeleniumWhenFinished(boolean stopSeleniumWhenFinished) {
		this.stopSeleniumWhenFinished = stopSeleniumWhenFinished;
	}

	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}

	public void setPreSelectedTest(Test test) {
		this.preSelectedTest = test;
	}

	public void setTargetExtensionPoint(ExtensionPoint targetExPoint) {
		this.targetExPoint = targetExPoint;
	}

	public void setCustomCompletedMessage(String customCompletedMessage) {
		this.customCompletedMessage = customCompletedMessage;
	}
	
	public void setShowCompletedMessageInStatusLine(boolean b) {
		this.showCompletedMessageInStatusLine = b;
	}
}
