/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
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
import org.eclipse.ui.IActionDelegate;

/**
 * Action for starting Selenium export. Will be in context menu in the 
 * Navigator or Package Explorer.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportToSeleniumAction implements IActionDelegate {
	ISelection selection;
	
	public static final String OK_MESSAGE = "Test exported OK to the \"generated\" directory.";

	
	/* 
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
			//callback to CubicTest with the selected files
			CubicTestExport.exportSelection((IStructuredSelection) selection, "html",
					UrlStartPointConverter.class, 
					TransitionConverter.class, 
					CustomTestStepConverter.class, 
					PageElementConverter.class, 
					ContextConverter.class,
					SeleneseDocument.class);
			UserInfo.showInfoDialog(OK_MESSAGE);
		} 
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error occured in CubicTest Selenium exporter.", e);
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
