/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.ui;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.ITestRunner;
import org.cubictest.export.utils.exported.ExportUtils;
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

/**
 * Action for running a test using the Watir runner.
 * 
 * @author Christian Schwarz
 */
public abstract class BaseRunnerAction implements IEditorActionDelegate {

	protected ITestEditor testEditor;
	protected ITestRunner testRunner;

	public BaseRunnerAction() {
		super();	
	}
		

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Shell shell = null;
		
		try {
			Test test = getTest();
			
			if( test == null ) {
				UserInfo.showErrorDialog("Could not get test. Close editor and retry");
				return;
			}
			if (!ExportUtils.testIsOkForExport(test)) {
				ExportUtils.showTestNotOkForExportMsg(test);
				return;
			}
			
			test.resetStatus();
			
			CubicTestProjectSettings settings = new CubicTestProjectSettings(testEditor.getProject());
		
			testRunner = getTestRunner(test, Display.getCurrent(), settings);

			shell = getShell();
			
			//run the test:
			new ProgressMonitorDialog(shell).run(true, true, testRunner);
			
			//show result:
			String result = ((ITestRunner) testRunner).getResultMessage();
			showCompletedMessage(shell, result);
		}
		catch (Exception e) {
			if (testRunner != null) {
				((ITestRunner) testRunner).getResultMessage();
			}
			if(shell != null) {
				shell.forceActive();
			}
			ErrorHandler.logAndShowErrorDialog(e, "Error when running test", shell);
		}
		finally {
			finalCleanUp();
		}
	}


	protected Test getTest() {
		return testEditor.getTest();
	}




	protected void showCompletedMessage(Shell shell, String result) {
		MessageDialog.openInformation(shell, "Test Runner done", result);
	}
	

	/**
	 * Set active editor and get the Test.
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null && targetEditor instanceof ITestEditor) {
			testEditor = ((ITestEditor) targetEditor);
		}
	}


	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	
	protected abstract ITestRunner getTestRunner(Test test, Display display, CubicTestProjectSettings settings);

	protected abstract Shell getShell();

	protected void finalCleanUp() {
	}
}
