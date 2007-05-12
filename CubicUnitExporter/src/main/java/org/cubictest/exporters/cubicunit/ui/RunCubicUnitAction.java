/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.cubicunit.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.exporters.cubicunit.CubicUnitRunnerPlugin;
import org.cubictest.exporters.cubicunit.runner.CubicUnitRunner;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Action for running CubicUnit test.
 * 
 * @author Christian Schwarz
 */
public class RunCubicUnitAction implements IEditorActionDelegate {

	Test test;

	public RunCubicUnitAction() {
		super();
		
	}
		
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Shell shell = CubicUnitRunnerPlugin.getDefault().
			getWorkbench().getActiveWorkbenchWindow().getShell();
		CubicUnitRunnerWizard wizard = new CubicUnitRunnerWizard();
		WizardDialog dlg = new WizardDialog(shell, wizard);
		
		if(dlg.open() != WizardDialog.CANCEL){	
			try {
				if( test != null ){
					
					test.resetStatus();
					
					Display display = Display.getCurrent();
					
					IRunnableWithProgress testRunner = 
						new CubicUnitRunner(test,display,
								wizard.getPort(),wizard.getBrowserType());
					
					new ProgressMonitorDialog(shell).run(true, true, testRunner);
				}
			}catch (Exception e) {
				if(shell != null)
					shell.forceActive();
				ErrorHandler.logAndShowErrorDialog(e, "Error when running test", shell);
			}
		}
	}
	
	/**
	 * Set active editor and get the Test.
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof ITestEditor) {
			test = ((ITestEditor) targetEditor).getTest();
		}
	}


	public void selectionChanged(IAction action, ISelection selection) {
	}
}
