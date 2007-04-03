package org.cubictest.exporters.selenium.runner.holders;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.holders.ContextHolder;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Holder for a Selenium session.
 *  
 * @author Christian Schwarz
 */
public class SeleniumHolder extends ContextHolder {

	private Selenium selenium;
	
	public SeleniumHolder(int port, String browser, String initialUrl) {
		if (port < 80) {
			throw new ExporterException("Invalid port");
		}
		selenium = new DefaultSelenium("localhost", port, browser, initialUrl);
	}
	
	public Selenium getSelenium() {
		return selenium;
	}
}
