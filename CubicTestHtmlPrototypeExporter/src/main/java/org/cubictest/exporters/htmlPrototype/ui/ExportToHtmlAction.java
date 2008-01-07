/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Erlend S. Halvorsen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Erlend S. Halvorsen - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.htmlPrototype.ui;

import org.cubictest.common.utils.ErrorHandler;
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
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error creating HTML prototype", e);
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
