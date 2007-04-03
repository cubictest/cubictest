/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.util;

import static org.cubictest.exporters.selenium.runner.util.SeleniumController.Operation.START;

import java.util.concurrent.Callable;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;

/**
 * Controller that starts/stops the Selenium Server and Selenium test runner.
 * 
 * @author Christian Schwarz
 */
public class SeleniumController implements Callable<SeleniumHolder> {

	public enum Operation {START, STOP};

	SeleniumRunnerServer server;
	SeleniumHolder seleniumHolder;
	public Operation operation = START;
	private String url;

	
	public SeleniumHolder call() throws InterruptedException {
		if (START.equals(operation)) {
			server = new SeleniumRunnerServer();
			server.start();
			while (!server.isStarted()) {
				//wait for server thread to start
				Thread.sleep(100);
			}

			Logger.info("Connecting to Selenium Proxy... Port " + server.getPort() + ", URL: " + url);
			seleniumHolder = new SeleniumHolder(server.getPort(), "*opera", url);
			seleniumHolder.getSelenium().start();
			seleniumHolder.getSelenium().open(url);
			return seleniumHolder;
		}
		else {
			//STOP
			try {
				if (seleniumHolder != null) {
					seleniumHolder.getSelenium().stop();
				}
			} 
			catch (Exception e) {
				ErrorHandler.logAndRethrow(e, "Error when stopping selenium test system");
			}
			finally {
				try {
					if (server != null) {
						server.stop();
					}
				} 
				catch (InterruptedException e) {
					ErrorHandler.logAndRethrow(e, "Error when stopping server");
				}
			}	
			return null;
		}
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
