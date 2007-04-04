package org.cubictest.exporters.selenium.runner.holders;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.utils.ContextHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Holder for a Selenium session.
 *  
 * @author Christian Schwarz
 */
public class SeleniumHolder extends ContextHolder {

	private Selenium selenium;
	private List<PageElement> elementsAsserted = new ArrayList<PageElement>();
	private List<TestPartStatus> results = new ArrayList<TestPartStatus>();
	private boolean seleniumStarted;
	
	public SeleniumHolder(int port, String browser, String initialUrl) {
		if (port < 80) {
			throw new ExporterException("Invalid port");
		}
		selenium = new DefaultSelenium("localhost", port, browser, initialUrl);
	}
	
	public Selenium getSelenium() {
		return selenium;
	}
	
	public void addResult(PageElement element, TestPartStatus result) {
		elementsAsserted.add(element);
		results.add(result);
	}
	
	public void showResults() {
		int i = 0;
		for (PageElement element : elementsAsserted) {
			element.setStatus(results.get(i));
			i++;
		}
	}

	public boolean isSeleniumStarted() {
		return seleniumStarted;
	}

	public void setSeleniumStarted(boolean seleniumStarted) {
		this.seleniumStarted = seleniumStarted;
	}
}
