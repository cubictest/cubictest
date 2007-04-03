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
	
	CubicSeleniumServer server;
	SeleniumHolder seleniumHolder;
	public Operation operation = START;
	private String url;
	private Browser browser;
	private boolean seleniumStarted;
	
	public SeleniumHolder call() throws InterruptedException {
		if (START.equals(operation)) {
			server = new CubicSeleniumServer();
			server.start();
			while (!server.isStarted()) {
				//wait for server thread to start
				Thread.sleep(100);
			}

			Logger.info("Connecting to Selenium Proxy... Port " + server.getPort() + ", URL: " + url);
			seleniumHolder = new SeleniumHolder(server.getPort(), browser.getId(), url);
			seleniumHolder.getSelenium().start();
			seleniumStarted = true;
			//check connection and that browser profiles has been set correctly.
			seleniumHolder.getSelenium().open(url);
			//two started variables, as one of them has sanity check of invoking start URL built into it.
			seleniumHolder.setSeleniumStarted(true);
			Logger.info("Connected to Selenium Proxy.");
			return seleniumHolder;
		}
		else {
			//STOP
			try {
				if (seleniumHolder != null && seleniumStarted) {
					seleniumHolder.getSelenium().stop();
					Logger.info("Closed connection to selenium proxy.");
					seleniumHolder.setSeleniumStarted(false);
					//two started variables, as one of them has sanity check of invoking start URL built into it.
					seleniumStarted = false;
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

	public void setStartUrl(String url) {
		this.url = url;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}
}
