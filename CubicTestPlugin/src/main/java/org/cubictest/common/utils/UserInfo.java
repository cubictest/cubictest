/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.common.utils;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.resources.UiText;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * Utility class for informing the user.
 * 
 * @author Christian Schwarz
 */
public class UserInfo {

	
	public static void showInfoDialog(String message) {
		try {
			MessageDialog.openInformation(new Shell(), UiText.APP_TITLE, message);
		}
		catch (Throwable t) {
			System.out.println("Could not show info dialog: " + message);
		}
	}

	public static void showWarnDialog(String message) {
		try {
			MessageDialog.openWarning(new Shell(), UiText.APP_TITLE, message);
		}
		catch (Throwable t) {
			System.out.println("Could not show warn dialog: " + message);
		}
	}



	public static void showErrorDialog(String userMessage) {
		try {
			MessageDialog.openError(new Shell(), UiText.APP_TITLE, userMessage);
		}
		catch (Throwable t) {
			System.out.println("Could not show message dialog: " + userMessage);
		}
	}

	public static void showErrorDialog(Throwable e) {
		e = ErrorHandler.getCause(e);
		try {
			MessageDialog.openError(new Shell(), UiText.APP_TITLE, e.toString());
		}
		catch (Throwable t) {
			System.out.println("Could not show message dialog: " + e.toString());
		}
	}
	
	public static void showErrorDialog(Throwable e, String userMessage) {
		try {
			showErrorDialog(e, userMessage, new Shell());
		}
		catch (Throwable t) {
			System.out.println("Could not show error dialog: " + e + ", message: " + userMessage);
		}
	}

	public static void showErrorDialog(Throwable e, String userMessage, Shell shell) {
		e = ErrorHandler.getCause(e);
		
		try {
			if (e == null) {
				MessageDialog.openError(shell, UiText.APP_TITLE, userMessage);
			}
			else {
				String extendedMsg = userMessage + ((e == null) ? "" : "\n\n" + e.toString());
				MessageDialog.openError(shell, UiText.APP_TITLE, extendedMsg);
			}
		}
		catch (Throwable t) {
			System.out.println("Could not show error dialog: " + e + ", message: " + userMessage);
		}
	}
	
	
	public static void setStatusLine(String msg) {
		IWorkbenchWindow window = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		((WorkbenchWindow) window).getStatusLineManager().setErrorMessage(msg);
	}

	public static void clearStatusLine() {
		IWorkbenchWindow window = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		((WorkbenchWindow) window).getStatusLineManager().setMessage("");
		((WorkbenchWindow) window).getStatusLineManager().setErrorMessage("");
	}
}
