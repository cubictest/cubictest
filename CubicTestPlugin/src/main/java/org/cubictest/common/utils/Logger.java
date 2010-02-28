/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.common.utils;

/**
 * Cubic Test logger that logs to the available log system 
 * (Eclipse log system if running in eclipse, otherwise Commons Logging). 
 * 
 * @author Christian Schwarz
 */
public class Logger {

	public static void error(String message) {
		if (EnvironmentInfo.isRunningInEclipse())
			EclipseLogger.error(message);
		else
			CommonsLoggingLogger.error(message);
			
	}
	
	public static void error(Throwable e) {
		if (EnvironmentInfo.isRunningInEclipse())
			EclipseLogger.error(e);
		else
			CommonsLoggingLogger.error(e);
	}

	public static void error(String message, Throwable e) {
		if (EnvironmentInfo.isRunningInEclipse())
			EclipseLogger.error(e, message);
		else
			CommonsLoggingLogger.error(e, message);
	}
	
	
	public static void warn(String message) {
		if (EnvironmentInfo.isRunningInEclipse())
			EclipseLogger.warn(message);
		else
			CommonsLoggingLogger.warn(message);
	}

	public static void warn(String message, Throwable e) {
		if (EnvironmentInfo.isRunningInEclipse())
			EclipseLogger.warn(e, message);
		else
			CommonsLoggingLogger.warn(e, message);
	}
	
	public static void info(String message) {
		if (EnvironmentInfo.isRunningInEclipse())
			EclipseLogger.info(message);
		else
			CommonsLoggingLogger.info(message);
	}

}
