/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.RunnerSetup;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.thoughtworks.selenium.Selenium;

/**
 * Action for running a test using the Selenium runner.
 * 
 * @author Christian Schwarz
 */
public class RunSeleniumRunnerAction implements IEditorActionDelegate {

	Test test;
	boolean stopSeleniumWhenFinished = true;
	Selenium selenium;
	private ExtensionPoint targetExPoint;
	private String customCompletedMessage;
	private boolean showCompletedMessageInStatusLine;

	public RunSeleniumRunnerAction() {
		super();	
	}
		

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if( test == null ) {
			ErrorHandler.showErrorDialog("Could not get test. Close editor and retry");
			return;
		}
		test.resetStatus();
		
		RunnerSetup testRunner = null;
		Shell shell = null;
		try {
			testRunner = new RunnerSetup(test, targetExPoint, Display.getCurrent());
			if (selenium != null) {
				testRunner.setSelenium(selenium);
			}
			shell = SeleniumExporterPlugin.getDefault().
					getWorkbench().getActiveWorkbenchWindow().getShell();
			
			//run the test:
			new ProgressMonitorDialog(shell).run(true, true, testRunner);
			
			//show result:
			String result = ((RunnerSetup) testRunner).showResults();
			showCompletedMessage(shell, result);
		}
		catch (Exception e) {
			if (testRunner != null) {
				((RunnerSetup) testRunner).showResults();
			}
			if(shell != null)
				shell.forceActive();
			ErrorHandler.logAndShowErrorDialog(e, "Error when running test", shell);
		}
		finally {
			if (stopSeleniumWhenFinished && testRunner != null) {
				((RunnerSetup) testRunner).stopSelenium();
			}
		}
	}


	private void showCompletedMessage(Shell shell, String result) {
		String msg = "Test run finished. " + result + ". Press OK to close test browser.";
		if (StringUtils.isNotBlank(customCompletedMessage)) {
			//use custom message instead
			msg = StringUtils.replace(customCompletedMessage, "$result", result); 
		}

		if (showCompletedMessageInStatusLine) {
			UserInfo.setStatusLine(msg);
		}
		else {
			shell.forceActive();
			MessageDialog.openInformation(shell, "CubicTest Selenium Exporter", msg);
		}
	}
	

	/**
	 * Set active editor and get the Test.
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null && targetEditor instanceof ITestEditor) {
			test = ((ITestEditor) targetEditor).getTest();
		}
	}


	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void setStopSeleniumWhenFinished(boolean stopSeleniumWhenFinished) {
		this.stopSeleniumWhenFinished = stopSeleniumWhenFinished;
	}


	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}


	public void setTest(Test test) {
		this.test = test;
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
