/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.util;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.runner.SeleniumRunnerConfiguration;
import org.openqa.selenium.server.SeleniumServer;

/**
 * Selenium proxy server used by the runner.
 * Runs as a thread.
 * 
 * @author Christian Schwarz
 */
public class SeleniumProxyServer {

	SeleniumServer seleniumServer;
	Thread serverThread;
	boolean started;
	SeleniumRunnerConfiguration config;
	
	
	public SeleniumProxyServer(SeleniumRunnerConfiguration config) {
		this.config = config;
		try {
			seleniumServer = new SeleniumServer(false, config.getRemoteControlConfiguration());

			final int portInfo = config.getSeleniumServerPort();
	        serverThread = new Thread(new Runnable() {
	            public void run() {
	                try {
	        			Logger.info("Starting selenium server at port " + portInfo);
	                    seleniumServer.start();
	                    started = true;
	                }
	                catch (Exception e) {
	                    ErrorHandler.logAndShowErrorDialogAndRethrow("Error starting selenium server.", e);
	                }
	            }
	        });
	        
		} catch (Exception e) {
            ErrorHandler.logAndRethrow("Error starting selenium server.", e);
		}
	}
	
	/**
	 * Starts the server.
	 */
	public void start() {
        serverThread.start();
		
	}
	
	/**
	 * Stops the server.
	 * @throws InterruptedException
	 */
	public void stop() throws InterruptedException{
		Logger.info("Stopping selenium server at port " + config.getSeleniumServerPort());
		seleniumServer.stop();
	}
	

	public int getPort() {
		return config.getSeleniumServerPort();
	}

	public boolean isStarted() {
		return started;
	}
}
