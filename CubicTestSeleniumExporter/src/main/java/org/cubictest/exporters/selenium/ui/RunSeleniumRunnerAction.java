/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.exporters.selenium.runner.RunnerSetup;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.UIPlugin;

/**
 * Action for running a test using the Selenium runner.
 * 
 * @author Christian Schwarz
 */
public class RunSeleniumRunnerAction implements IEditorActionDelegate {

	Test test;

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
		
		IRunnableWithProgress testRunner = null;
		Shell shell = null;
		try {
			testRunner = new RunnerSetup(test, Display.getCurrent());
			shell = UIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			
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
			shell.forceActive();
			ErrorHandler.logAndShowErrorDialog(e, "Error when running test", shell);
		}
		finally {
			((RunnerSetup) testRunner).stopSelenium();
		}
	}


	private void showCompletedMessage(Shell shell, String result) {
		shell.forceActive();
		MessageDialog.openInformation(shell, "CubicTest Selenium Exporter", 
				"Test run finished. " + result + "\n" +
						"Press OK to close test browser.");
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
}
