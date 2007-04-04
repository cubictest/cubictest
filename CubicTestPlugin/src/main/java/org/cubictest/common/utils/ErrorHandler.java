/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.resources.UiText;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Util class for handling exceptions (show error message to user, log error, rethrow etc.)
 * 
 * @author chr_schwarz
 *
 */
public class ErrorHandler {

	public static void rethrow(Throwable e) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		e = getCause(e);
		throw new CubicException(e);
	}

	public static void rethrow(Throwable e, String message) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		e = getCause(e);
		throw new CubicException(e, message);
	}
	
	public static void logAndRethrow(Throwable e) {
		Logger.error(e, e.getMessage());
		rethrow(e);
	}

	public static void logAndThrow(String message) {
		Logger.error(null, message);
		throw new CubicException(message);
	}
	
	public static void logAndRethrow(Throwable e, String message) {
		Logger.error(e, message);
		rethrow(e, message);
	}

	public static void logAndShowErrorDialog(String message) {
		Logger.error(null, message);
		showErrorDialog(null, message);
	}
	
	public static void logAndShowErrorDialog(Throwable e) {
		Logger.error(e, e.getMessage());
		showErrorDialog(e);
	}
	
	public static void logAndShowErrorDialog(Throwable e, String message) {
		Logger.error(e, message);
		showErrorDialog(e, message);
	}

	public static void logAndShowErrorDialog(Throwable e, String message, Shell shell) {
		Logger.error(e, message);
		showErrorDialog(e, message, shell);
	}

	
	public static void logAndShowErrorDialogAndThrow(String message) {
		Logger.error(null, message);
		showErrorDialog(message);
		throw new CubicException(message);
	}
	
	public static void logAndShowErrorDialogAndRethrow(Throwable e) {
		Logger.error(e, e.getMessage());
		showErrorDialog(e);
		rethrow(e);
	}

	
	public static void logAndShowErrorDialogAndRethrow(Throwable e, String message) {
		Logger.error(e, message);
		showErrorDialog(e, message);
		rethrow(e, message);
	}
	
	public static void showErrorDialog(Throwable e) {
		e = getCause(e);
		try {
			MessageDialog.openError(new Shell(), UiText.APP_TITLE, e.toString());
		}
		catch (Throwable t) {
			System.out.println("Could not show message dialog: " + e.toString());
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

	public static void showErrorDialog(Throwable e, String userMessage) {
		showErrorDialog(e, userMessage, new Shell());
	}
	
	public static void showErrorDialog(Throwable e, String userMessage, Shell shell) {
		e = getCause(e);
		String msg = null;
		
		try {
			if(e instanceof InterruptedException) {
				msg = UiText.INTERRUPTED_MESSAGE;
			}
			else {
				msg = userMessage + ": " + ((e == null) ? "" : "\n" + e.toString());
			}
			CubicTestPlugin plugin = CubicTestPlugin.getDefault();
			IStatus status = new Status(IStatus.ERROR, plugin.getId(), IStatus.OK, userMessage, e);
			ErrorDialog.openError(shell, UiText.APP_TITLE, msg, status);
		}
		catch (Throwable t) {
			System.out.println("Could not show message dialog: " + msg);
		}
	}

	public static Throwable getCause(Throwable e) {
		if (e instanceof InvocationTargetException) {
			return ((InvocationTargetException) e).getCause();
		}
		return e;
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
