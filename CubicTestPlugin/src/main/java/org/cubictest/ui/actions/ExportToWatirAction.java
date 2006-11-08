/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.actions;

import org.cubictest.export.watir.ExporterSetup;
import org.cubictest.ui.eclipse.ExceptionHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.ide.IDE;



public class ExportToWatirAction implements IActionDelegate {
	ISelection selection;
	
	/**
	 * Constructor for Action1.
	 */
	public ExportToWatirAction() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Shell shell = new Shell();
		
		IResource res = (IResource) ((IStructuredSelection)selection).getFirstElement();
		
		IDE.saveAllEditors(new IResource[] {res}, true);

		
		try {

			IRunnableWithProgress exporter = ExporterSetup.getRunnableExporter(res);

			new ProgressMonitorDialog(shell).run(false, false, exporter);
			
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
