/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.resources.UiText;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
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
			CubicTestPlugin plugin = CubicTestPlugin.getDefault();
	
			if (e == null) {
				MessageDialog.openError(shell, UiText.APP_TITLE, userMessage);
			}
			else {
				String extendedMsg = userMessage + ((e == null) ? "" : ": \n" + e.toString());
				IStatus status = new Status(IStatus.ERROR, plugin.getId(), IStatus.OK, userMessage, e);
				ErrorDialog.openError(shell, UiText.APP_TITLE, extendedMsg, status);
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
