/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.exception.CubicException;
import org.eclipse.swt.widgets.Shell;

/**
 * Util class for handling exceptions (show error message to user, log error, rethrow etc.)
 * 
 * @author chr_schwarz
 *
 */
public class ErrorHandler {

	public static void rethrow(Throwable e) {
		e = getCause(e);
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		throw new CubicException(e);
	}

	public static void rethrow(Throwable e, String message) {
		e = getCause(e);
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
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
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(null, message);
		}
	}
	
	public static void logAndShowErrorDialog(Throwable e) {
		Logger.error(e, e.getMessage());
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e);
		}
	}
	
	public static void logAndShowErrorDialog(Throwable e, String message) {
		Logger.error(e, message);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e, message);
		}
	}

	public static void logAndShowErrorDialog(Throwable e, String message, Shell shell) {
		Logger.error(e, message);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e, message, shell);
		}
	}

	
	public static void logAndShowErrorDialogAndThrow(String message) {
		Logger.error(null, message);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(message);
		}
		throw new CubicException(message);
	}
	
	public static void logAndShowErrorDialogAndRethrow(Throwable e) {
		Logger.error(e, e.getMessage()); 
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e);
		}
		rethrow(e);
	}

	
	public static void logAndShowErrorDialogAndRethrow(Throwable e, String message) {
		Logger.error(e, message);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e, message);
		}
		rethrow(e, message);
	}
	
	
	
	public static Throwable getCause(Throwable e) {
		if (e instanceof InvocationTargetException) {
			return ((InvocationTargetException) e).getCause();
		}
		return e;
	}
}
