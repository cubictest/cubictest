/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import org.cubictest.CubicTestPlugin;
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
}
