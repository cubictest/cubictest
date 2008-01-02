/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for logging to commons logging.
 * 
 * @author Christian Schwarz
 */
public class CommonsLoggingLogger {

	private static Log log = LogFactory.getLog(CommonsLoggingLogger.class);
	
	
	public static void error(String message) {
		String newMessage = StringUtils.isBlank(message) ? "An error has occurred" : message;
		log.error(newMessage);
	}
	
	public static void error(Throwable e) {
		e = ErrorHandler.getCause(e);
		log.error(e.toString(), e);
	}

	public static void error(Throwable e, String message) {
		e = ErrorHandler.getCause(e);
		message = StringUtils.isBlank(message) ? "An error has occurred" : message;
		log.error(message, e);
	}
	
	
	public static void warn(String message) {
		log.warn(message);
	}

	public static void warn(Throwable e, String message) {
		e = ErrorHandler.getCause(e);
		log.warn(message, e);
	}
	
	public static void info(String message) {
		log.info(message);
	}
	
}
