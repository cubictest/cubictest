/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.CubicTestExport;
import org.cubictest.exporters.htmlPrototype.ExporterSetup;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Action for exporting to HTML prototype.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportToHtmlEditorAction implements IEditorActionDelegate {
	IResource currentFile;
	
	public ExportToHtmlEditorAction() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
			IDE.saveAllEditors(new IResource[] { currentFile }, true);

			//callback to CubicTest with the selected files
			CubicTestExport.exportWithCustomDirectoryWalker(ExporterSetup.getRunnableExporter(currentFile));
		} 
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error creating HTML prototype", e);
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		currentFile = ((FileEditorInput)targetEditor.getEditorInput()).getFile();
	}
}
