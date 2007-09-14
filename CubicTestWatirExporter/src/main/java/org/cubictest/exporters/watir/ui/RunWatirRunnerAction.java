/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.ui;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.watir.WatirExporterPlugin;
import org.cubictest.exporters.watir.runner.RunnerSetup;
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
public class RunWatirRunnerAction implements IEditorActionDelegate {

	ITestEditor testEditor;

	public RunWatirRunnerAction() {
		super();	
	}
		

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		RunnerSetup testRunner = null;
		Shell shell = null;
		
		try {
			Test test = testEditor.getTest();
			
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
		
			testRunner = new RunnerSetup(test, Display.getCurrent(), settings);

			shell = WatirExporterPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			
			//run the test:
			new ProgressMonitorDialog(shell).run(true, true, testRunner);
			
			//show result:
			String result = ((RunnerSetup) testRunner).getResultInfo();
			showCompletedMessage(shell, result);
		}
		catch (Exception e) {
			if (testRunner != null) {
				((RunnerSetup) testRunner).getResultInfo();
			}
			if(shell != null) {
				shell.forceActive();
			}
			ErrorHandler.logAndShowErrorDialog(e, "Error when running test", shell);
		}
	}


	private void showCompletedMessage(Shell shell, String result) {
		MessageDialog.openInformation(shell, "Watir Test Runner done", result);
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

}
