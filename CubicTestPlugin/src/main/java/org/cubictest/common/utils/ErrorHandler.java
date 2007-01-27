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
		log(IStatus.ERROR, e, e.getMessage());
		rethrow(e);
	}

	public static void logAndRethrow(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
		rethrow(e, message);
	}

	public static void logAndShowErrorDialog(Throwable e) {
		log(IStatus.ERROR, e, e.getMessage());
		showErrorDialog(e);
	}

	public static void logAndShowErrorDialog(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
		showErrorDialog(e, message);
	}
	
	public static void logAndShowErrorDialogAndRethrow(Throwable e) {
		log(IStatus.ERROR, e, e.getMessage());
		showErrorDialog(e);
		rethrow(e);
	}

	public static void logAndShowErrorDialogAndRethrow(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
		showErrorDialog(e, message);
		rethrow(e, message);
	}
	
	public static void showErrorDialog(Throwable e) {
		e = getCause(e);
		MessageDialog.openError(new Shell(), UiText.APP_TITLE, e.toString());
	}

	public static void showErrorDialog(String userMessage) {
		MessageDialog.openError(new Shell(), UiText.APP_TITLE, userMessage);
	}

	
	public static void showErrorDialog(Throwable e, String userMessage) {
		e = getCause(e);
		if(e instanceof InterruptedException) {
			MessageDialog.openError(new Shell(), UiText.APP_TITLE, UiText.INTERRUPTED_MESSAGE);
		}
		else {
			MessageDialog.openError(new Shell(), UiText.APP_TITLE, userMessage + ": " +
					((e == null) ? "" : "\n" + e.toString()));
		}
	}


	public static void log(Throwable e) {
		log(IStatus.ERROR, e, e.toString());
	}

	public static void log(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
	}
	
	/**
	 * Log an error to the Eclipse log system and a printStackTrace.
	 * 
	 * @param severity the severity; one of <code>IStatus.OK</code>, <code>IStatus.ERROR</code>, 
	 * <code>IStatus.INFO</code>, <code>IStatus.WARNING</code>,  or <code>IStatus.CANCEL</code>
	 * @param e the throwable to log
	 * @param message the message (humanly readable)
	 */
	public static void log(int severity, Throwable e, String message) {
		e = getCause(e);
		e.printStackTrace();
		CubicTestPlugin plugin = CubicTestPlugin.getDefault();
		IStatus status = new Status(severity, plugin.getId(), IStatus.OK, message, e);
		plugin.getLog().log(status);
	}

	private static Throwable getCause(Throwable e) {
		if (e instanceof InvocationTargetException) {
			return ((InvocationTargetException) e).getCause();
		}
		return e;
	}
}
