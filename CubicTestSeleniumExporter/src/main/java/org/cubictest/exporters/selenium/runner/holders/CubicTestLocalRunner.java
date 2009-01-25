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
package org.cubictest.exporters.selenium.runner.holders;

import java.lang.reflect.Method;

import org.cubictest.export.exceptions.ExporterException;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class CubicTestLocalRunner {

	private Selenium selenium;

	public CubicTestLocalRunner(String seleniumServerHostname, int seleniumServerPort, String browser, String initialUrl) {
		selenium = new DefaultSelenium(seleniumServerHostname, seleniumServerPort, browser, initialUrl);
	}

	public CubicTestLocalRunner(Selenium selenium) {
		this.selenium = selenium;
	}

	public String execute(String commandName, String locator, String inputValue) {
		try {
			Method method = selenium.getClass().getMethod(commandName, new Class[]{String.class, String.class});
			return method.invoke(selenium, new Object[]{locator, inputValue}) + "";
		} catch (Exception e) {
			throw new ExporterException(e);
		}
	}

	public String[] execute(String commandName, String... vars){
		Class<?>[] classes = new Class[vars.length];
		
		for(int i = 0; i < vars.length; i++){
			classes[i] = vars[i].getClass();
		}
		
		try {
			Method method = selenium.getClass().getMethod(commandName, classes);
			Object result = method.invoke(selenium, (Object[])vars);
			if(result instanceof String[]){
				return (String[]) result;
			}
			return new String[]{result + ""};
		} catch (Exception e) {
			throw new ExporterException(e);
		}
	}
	
	public String execute(String commandName, String locator) {
		try {
			Method method = selenium.getClass().getMethod(commandName, new Class[]{String.class});
			return method.invoke(selenium, new Object[]{locator}) + "";
		} catch (Exception e) {
			throw new ExporterException(e);
		}
	}

	public String getText(String locator) {
		return selenium.getText(locator);
	}

	public String getTitle() {
		return selenium.getTitle();
	}

	public String getValue(String locator) {
		return selenium.getValue(locator);
	}

	public boolean isTextPresent(String text) {
		return selenium.isTextPresent(text);
	}

	public boolean isElementPresent(String locator) {
		return selenium.isElementPresent(locator);
	}

	public void waitForPageToLoad(String string) {
		selenium.waitForPageToLoad(string);
	}

	public void open(String beginAt) {
		selenium.open(beginAt);
	}
	
	public void selectFrame(String locator) {
		selenium.selectFrame(locator);
	}

	public void setTimeout(String string) {
		selenium.setTimeout(string);
	}

	public void start() {
		selenium.start();		
	}

	public void stop() {
		selenium.stop();
	}

	public Selenium getSelenium() {
		return selenium;
	}
}
