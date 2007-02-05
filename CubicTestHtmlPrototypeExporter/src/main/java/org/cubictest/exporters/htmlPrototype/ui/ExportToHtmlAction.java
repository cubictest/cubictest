/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.ui;

import org.cubictest.export.CubicTestExport;
import org.cubictest.exporters.htmlPrototype.ExporterSetup;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.ide.IDE;

/**
 * Action for exporting to HTML prototype.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportToHtmlAction implements IActionDelegate {
	ISelection selection;
	
	public ExportToHtmlAction() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
			IResource res = (IResource) ((IStructuredSelection)selection).getFirstElement();
			IDE.saveAllEditors(new IResource[] { res }, true);

			//callback to CubicTest with the selected files
			CubicTestExport.exportWithCustomDirectoryWalker(ExporterSetup.getRunnableExporter(res));
		} 
		catch (Exception e) {
			MessageDialog.openError(new Shell(), "CubicTest HTML prototype exporter", "An error has occurred!" + "\n" + e.toString());
			e.printStackTrace();
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
