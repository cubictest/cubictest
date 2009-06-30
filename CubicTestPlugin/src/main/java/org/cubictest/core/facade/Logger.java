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
package org.cubictest.core.facade;


/**
 * Published class that logs to the available log system 
 * (Eclipse log system if running in Eclipse, otherwise Commons Logging). 
 */
public class Logger {
	
	public static void error(String message) {
			org.cubictest.common.utils.Logger.error(message);
	}
	
	public static void error(Throwable t) {
			org.cubictest.common.utils.Logger.error(t);
	}

	public static void error(String message, Throwable t) {
			org.cubictest.common.utils.Logger.error(message, t);
	}
	
	
	public static void warn(String message) {
			org.cubictest.common.utils.Logger.warn(message);
	}

	public static void warn(String message, Throwable t) {
			org.cubictest.common.utils.Logger.warn(message, t);
	}
	
	public static void info(String message) {
			org.cubictest.common.utils.Logger.info(message);
	}

	
}
