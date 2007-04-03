/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import org.apache.commons.lang.StringUtils;
import org.cubictest.CubicTestPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Cubic Test logger that logs to the Eclipse log system (if available) or to System.out.
 * 
 * @author chr_schwarz
 */
public class Logger {

	public static void error(Throwable e) {
		log(IStatus.ERROR, e, e.toString());
	}

	public static void error(Throwable e, String message) {
		log(IStatus.ERROR, e, message);
	}
	
	
	public static void warn(String message) {
		log(IStatus.WARNING, null, message);
	}

	public static void warn(Throwable e, String message) {
		log(IStatus.WARNING, e, message);
	}
	
	public static void info(String message) {
		log(IStatus.INFO, null, message);
	}
	
	
	
	/**
	 * Internal method to log to the Eclipse log system (if available) and to System.out.
	 * 
	 * @param severity the severity; one of <code>IStatus.OK</code>, <code>IStatus.ERROR</code>, 
	 * <code>IStatus.INFO</code>, <code>IStatus.WARNING</code>,  or <code>IStatus.CANCEL</code>
	 * @param e the throwable to log
	 * @param message the message (humanly readable)
	 */
	private static void log(int severity, Throwable e, String message) {
		if (StringUtils.isBlank(message)) {
			message = "An error has occurred";
		}
		
		//Log to System.out:
		System.out.println(getLogLevel(severity) + ": " + message + ((e == null) ? "" : (", " + e)));

		if (e != null) {
			e.printStackTrace();
			e = ErrorHandler.getCause(e);
		}
		CubicTestPlugin plugin = CubicTestPlugin.getDefault();
		if (plugin != null) {
			//Log to Eclipse:
			IStatus status = new Status(severity, plugin.getId(), IStatus.OK, message, e);
			plugin.getLog().log(status);
		}
	}

	
	private static String getLogLevel(int severity) {
		if (severity == IStatus.ERROR)
			return "Error";
		
		if (severity == IStatus.WARNING)
			return "Warn";

		if (severity == IStatus.INFO)
			return "Info";
		
		return "Unknown log level";
	}
}
