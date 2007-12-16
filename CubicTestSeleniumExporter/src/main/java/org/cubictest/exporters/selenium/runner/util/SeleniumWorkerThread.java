/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.util;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.runner.RunnerWorkerThread;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.UrlStartPoint;

import com.thoughtworks.selenium.Selenium;

/**
 * Controller that starts/stops the Selenium Server and Selenium test system (SeleniumHolder).
 * Implements callable, and hence supports timeout of start/stop of Selenium.
 * 
 * @author Christian Schwarz
 */
public class SeleniumWorkerThread extends RunnerWorkerThread<SeleniumHolder> {

	SeleniumProxyServer server;
	SeleniumHolder seleniumHolder;
	UrlStartPoint initialUrlStartPoint;
	BrowserType browser;
	boolean seleniumStarted;
	Selenium selenium;
	private int port;
	
	
	/**
	 * Start the Selenium Proxy Server and start and return the Selenium test object.
	 */
	@Override
	public SeleniumHolder doStart() {
		if (selenium == null) {
			server = new SeleniumProxyServer(settings, port);
			server.start();
			while (!server.isStarted()) {
				//wait for server thread to start
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new ExporterException(e);
				}
			}
		}
		else {
			//We got a Selenium from Client process. It should already have a proxy server configured and started
		}

		if (selenium == null) {
			Logger.info("Opening test browser and connecting to Selenium Proxy... Port " + server.getPort() + ", URL: " + initialUrlStartPoint);
			seleniumHolder = new SeleniumHolder(port, browser.getId(), initialUrlStartPoint.getBeginAt(), display, settings);
			seleniumHolder.getSelenium().start();
			int timeout = SeleniumUtils.getTimeout(settings) * 1000;
			seleniumHolder.getSelenium().setTimeout(timeout + "");

			//open start URL and check connection (that browser profiles has been set correctly):
			seleniumHolder.getSelenium().open(initialUrlStartPoint.getBeginAt());
		}
		else {
			//use custom Selenium, e.g. from the CubicRecorder.
			Logger.info("Using Selenium from another plugin.");
			seleniumHolder = new SeleniumHolder(selenium, display, settings);
		}
		seleniumStarted = true;
		
		//using two started variables, as one of them has sanity check of invoking start URL built into it.
		seleniumHolder.setSeleniumStarted(true);
		
		Logger.info("Connected to Selenium Proxy.");
		seleniumHolder.setHandledUrlStartPoint(initialUrlStartPoint);
		return seleniumHolder;
	}
		
	
	
	@Override
	protected SeleniumHolder doStop() {
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
			ErrorHandler.logAndRethrow("Error when stopping selenium test system", e);
		}
		finally {
			try {
				if (server != null) {
					server.stop();
				}
			} 
			catch (InterruptedException e) {
				ErrorHandler.logAndRethrow("Error when stopping server", e);
			}
		}	
		return null;
	}

	
	public void setInitialUrlStartPoint(UrlStartPoint initialUrlStartPoint) {
		this.initialUrlStartPoint = initialUrlStartPoint;
	}

	public void setBrowser(BrowserType browser) {
		this.browser = browser;
	}

	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}



	public void setPort(int port) {
		this.port = port;
		
	}
}
