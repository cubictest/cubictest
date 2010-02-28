/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.CubicTestExport;
import org.cubictest.exporters.selenium.selenese.converters.ContextConverter;
import org.cubictest.exporters.selenium.selenese.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.selenese.converters.PageElementConverter;
import org.cubictest.exporters.selenium.selenese.converters.TransitionConverter;
import org.cubictest.exporters.selenium.selenese.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Action for exporting to Selenium's Selenese HTML table format.
 * Context menu in the Graphical test editor.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportToSeleneseEditorAction implements IEditorActionDelegate {
	ISelection selection;
	
	public ExportToSeleneseEditorAction() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selection == null) {
			UserInfo.showErrorDialog("Could not export test (selection unavailable).");
		}
		
		try {
			//callback to CubicTest with the selected files
			CubicTestExport.exportSelection((IStructuredSelection) selection, "html",
					UrlStartPointConverter.class, 
					TransitionConverter.class, 
					CustomTestStepConverter.class, 
					PageElementConverter.class, 
					ContextConverter.class,
					SeleneseDocument.class);
			UserInfo.showInfoDialog(ExportToSeleneseAction.OK_MESSAGE);
		} 
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error occured in Selenium export.", e);
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null) {
			selection = new StructuredSelection(((FileEditorInput)targetEditor.getEditorInput()).getFile());
		}
	}

}
