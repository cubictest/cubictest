/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.CubicTestExport;
import org.cubictest.exporters.selenium.converters.ContextConverter;
import org.cubictest.exporters.selenium.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.converters.PageElementConverter;
import org.cubictest.exporters.selenium.converters.TransitionConverter;
import org.cubictest.exporters.selenium.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.holders.SeleneseDocument;
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
	
	public static final String OK_MESSAGE = "Test exported OK to the \"generated\" directory.\n\n" +
			"The exported test (.html file) can be run by copying it into the Selenium test folder of your web application " +
			"and by adding it to a Selenium test suite.";

	
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
			ErrorHandler.showInfoDialog(OK_MESSAGE);
		} 
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error occured in CubicTest Selenium exporter.");
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
