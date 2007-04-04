/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.util;

import java.io.IOException;
import java.net.ServerSocket;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.openqa.selenium.server.SeleniumServer;

/**
 * Selenium proxy server used by the runner.
 * Runs as a thread.
 * 
 * @author Christian Schwarz
 */
public class CubicSeleniumServer {

	SeleniumServer seleniumServer;
	Thread serverThread;
	int port;
	boolean started;
	
	public CubicSeleniumServer() {
		try {
			//port = findAvailablePort();
			port = 28242; //cubic
			seleniumServer = new SeleniumServer(port);
			
	        serverThread = new Thread(new Runnable() {
	            public void run() {
	                try {
	        			Logger.info("Starting selenium server at port " + port);
	                    seleniumServer.start();
	                    started = true;
	                    Logger.info("Server started");
	                }
	                catch (Exception e) {
	                    ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error starting selenium server.");
	                }
	            }
	        });
	        
		} catch (Exception e) {
            ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error starting selenium server.");
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
	
	
	private int findAvailablePort() throws IOException {
		ServerSocket s = new ServerSocket();
		s.bind(null);
		int port = s.getLocalPort();
		s.close();
		return port;
	}

	public int getPort() {
		return port;
	}

	public boolean isStarted() {
		return started;
	}
}
