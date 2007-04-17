/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.util;

import static org.cubictest.exporters.selenium.runner.util.SeleniumController.Operation.START;
import static org.cubictest.exporters.selenium.runner.util.SeleniumController.Operation.STOP;

import java.util.concurrent.Callable;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.swt.widgets.Display;

/**
 * Controller that starts/stops the Selenium Server and Selenium test system (SeleniumHolder).
 * Implements callable, and hence supports timeout of start/stop of Selenium.
 * 
 * @author Christian Schwarz
 */
public class SeleniumController implements Callable<SeleniumHolder> {

	public enum Operation {START, STOP};
	
	CubicSeleniumServer server;
	SeleniumHolder seleniumHolder;
	public Operation operation = START;
	private UrlStartPoint initialUrlStartPoint;
	private Browser browser;
	private boolean seleniumStarted;
	private Display display;
	
	/**
	 * Method to start/stop the Selenium proxy server and Selenium test system.
	 * Mehtod will be guarded by a timeout (ensured by client).
	 * If operation field is set to START, returns a SeleniumHolder with a started Selenium test browser.
	 */
	public SeleniumHolder call() throws InterruptedException {
		if (START.equals(operation)) {
			server = new CubicSeleniumServer();
			server.start();
			while (!server.isStarted()) {
				//wait for server thread to start
				Thread.sleep(100);
			}

			Logger.info("Opening test browser and connecting to Selenium Proxy... Port " + server.getPort() + ", URL: " + initialUrlStartPoint);
			seleniumHolder = new SeleniumHolder(server.getPort(), browser.getId(), initialUrlStartPoint.getBeginAt(), display);
			seleniumHolder.getSelenium().start();
			seleniumStarted = true;
			
			//open start URL and check connection (that browser profiles has been set correctly):
			seleniumHolder.getSelenium().open(initialUrlStartPoint.getBeginAt());
			
			//using two started variables, as one of them has sanity check of invoking start URL built into it.
			seleniumHolder.setSeleniumStarted(true);
			
			Logger.info("Connected to Selenium Proxy.");
			seleniumHolder.setHandledUrlStartPoint(initialUrlStartPoint);
			return seleniumHolder;
		}
		
		else if (STOP.equals(operation)){
			try {
				if (seleniumHolder != null && seleniumStarted) {
					seleniumHolder.getSelenium().stop();
					Logger.info("Closed test browser");
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
		}
		return null;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public void setInitialUrlStartPoint(UrlStartPoint initialUrlStartPoint) {
		this.initialUrlStartPoint = initialUrlStartPoint;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}
}
