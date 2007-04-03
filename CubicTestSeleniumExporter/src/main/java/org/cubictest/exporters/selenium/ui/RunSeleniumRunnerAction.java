/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.exporters.selenium.runner.SeleniumRunner;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.EditorPluginAction;

/**
 * Action for running CubicUnit test.
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

		IRunnableWithProgress testRunner = null;
		try {
			testRunner = new SeleniumRunner(test);
			
			new ProgressMonitorDialog(new Shell()).run(true, true, testRunner);
			
			((SeleniumRunner) testRunner).showResults();
		}
		catch (Exception e) {
			if (testRunner != null) {
				((SeleniumRunner) testRunner).showResults();
			}
			ErrorHandler.logAndShowErrorDialog(e);
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
}
