/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.resources.UiText;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * Informs the user.
 * 
 * @author Christian Schwarz
 */
public class UserInfo {

	
	public static void setStatusLine(String msg) {
		IWorkbenchWindow window = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		((WorkbenchWindow) window).getStatusLineManager().setErrorMessage(msg);
	}

	public static void clearStatusLine() {
		IWorkbenchWindow window = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		((WorkbenchWindow) window).getStatusLineManager().setMessage("");
		((WorkbenchWindow) window).getStatusLineManager().setErrorMessage("");
	}
	
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
}
