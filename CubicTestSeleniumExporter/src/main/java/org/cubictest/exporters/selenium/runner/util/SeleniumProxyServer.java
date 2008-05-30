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
	int port;
	boolean started;
	
	
	public SeleniumProxyServer(boolean proxyInjectionMode, int port, boolean seleniumMultiWindow) {
		try {
			this.port = port;
			seleniumServer = new SeleniumServer(port,false,seleniumMultiWindow);
			seleniumServer.setProxyInjectionMode(proxyInjectionMode);
			
			final int portInfo = port;
	        serverThread = new Thread(new Runnable() {
	            public void run() {
	                try {
	        			Logger.info("Starting selenium server at port " + portInfo);
	                    seleniumServer.start();
	                    started = true;
	                    Logger.info("Server started");
	                }
	                catch (Exception e) {
	                    ErrorHandler.logAndShowErrorDialogAndRethrow("Error starting selenium server.", e);
	                }
	            }
	        });
	        
		} catch (Exception e) {
            ErrorHandler.logAndShowErrorDialogAndRethrow("Error starting selenium server.", e);
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
		Logger.info("Stopping selenium server at port " + port);
		seleniumServer.stop();
	}
	

	public int getPort() {
		return port;
	}

	public boolean isStarted() {
		return started;
	}
}
