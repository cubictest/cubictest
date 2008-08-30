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

	public static void rethrow(String message, Throwable e) {
		e = getCause(e);
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		throw new CubicException(e, message);
	}
	
	public static void logAndRethrow(Throwable e) {
		Logger.error(e.getMessage(), e);
		rethrow(e);
	}

	public static void logAndThrow(String message) {
		Logger.error(message, null);
		throw new CubicException(message);
	}
	
	public static void logAndRethrow(String message, Throwable e) {
		Logger.error(message, e);
		rethrow(message, e);
	}

	public static void logAndShowErrorDialog(String message) {
		Logger.error(message, null);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(null, message);
		}
	}
	
	public static void logAndShowErrorDialog(Throwable e) {
		Logger.error(e.getMessage(), e);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e);
		}
	}
	
	public static void logAndShowErrorDialog(String message, Throwable e) {
		Logger.error(message, e);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e, message);
		}
	}

	public static void logAndShowErrorDialog(String message, Throwable e, Shell shell) {
		Logger.error(message, e);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e, message, shell);
		}
	}

	
	public static void logAndShowErrorDialogAndThrow(String message) {
		Logger.error(message, null);
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(message);
		}
		throw new CubicException(message);
	}
	
	public static void logAndShowErrorDialogAndRethrow(Throwable e) {
		Logger.error(e.getMessage(), e); 
		if (EnvironmentInfo.isRunningInEclipse()) {
			UserInfo.showErrorDialog(e);
		}
		rethrow(e);
	}

	
	public static void logAndShowErrorDialogAndRethrow(String message, Throwable e) {
		logAndShowErrorDialogAndRethrow(message, e, null);
	}
	
	public static void logAndShowErrorDialogAndRethrow(String message, Throwable e, Shell shell) {
		Logger.error(message, e);
		if (EnvironmentInfo.isRunningInEclipse()) {
			if (shell == null) {
				shell = new Shell();
			}
			UserInfo.showErrorDialog(e, message, shell);
		}
		rethrow(message, e);
	}
	
	
	
	public static Throwable getCause(Throwable e) {
		if (e instanceof InvocationTargetException) {
			return ((InvocationTargetException) e).getCause();
		}
		return e;
	}
}
