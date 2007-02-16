/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;
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

	public static void logAndShowErrorDialog(String message) {
		log(IStatus.ERROR, null, message);
		showErrorDialog(null, message);
	}
	
	public static void logAndShowErrorDialog(Throwable e) {
		log(IStatus.ERROR, e, e.getMessage());
		showErrorDialog(e);
	}

	public static void logAndShowErrorDialog(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
		showErrorDialog(e, message);
	}
	
	public static void logAndShowErrorDialogAndThrow(String message) {
		log(IStatus.ERROR, null, message);
		showErrorDialog(message);
		throw new CubicException(message);
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
		e = getCause(e);
		String msg = null;
		
		try {
			if(e instanceof InterruptedException) {
				msg = UiText.INTERRUPTED_MESSAGE;
			}
			else {
				msg = userMessage + ": " + ((e == null) ? "" : "\n" + e.toString());
			}
			MessageDialog.openError(new Shell(), UiText.APP_TITLE, msg);
		}
		catch (Throwable t) {
			System.out.println("Could not show message dialog: " + msg);
		}
	}


	public static void log(Throwable e) {
		log(IStatus.ERROR, e, e.toString());
	}

	public static void log(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
	}
	
	
	public static void logWarning(String message) {
		log(IStatus.WARNING, null, message);
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
		if (StringUtils.isBlank(message)) {
			message = "An error has occurred";
		}
		
		System.out.println(message);
		if (e != null) {
			e.printStackTrace();
			e = getCause(e);
		}
		CubicTestPlugin plugin = CubicTestPlugin.getDefault();
		if (plugin != null) {
			IStatus status = new Status(severity, plugin.getId(), IStatus.OK, message, e);
			plugin.getLog().log(status);
		}
		else {
			System.out.println("Could not log error: " + message + ", " + e);
		}
	}

	private static Throwable getCause(Throwable e) {
		if (e instanceof InvocationTargetException) {
			return ((InvocationTargetException) e).getCause();
		}
		return e;
	}
}