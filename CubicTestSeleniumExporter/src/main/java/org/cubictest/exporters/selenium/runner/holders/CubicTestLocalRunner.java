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
import org.cubictest.exporters.selenium.runner.ICubicTestRunner;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class CubicTestLocalRunner implements ICubicTestRunner {

	private Selenium selenium;

	public CubicTestLocalRunner(String string, int port, String browser,
			String initialUrl) {
		selenium = new DefaultSelenium("localhost", port, browser, initialUrl);
	}

	public CubicTestLocalRunner(Selenium selenium) {
		this.selenium = selenium;
	}

	@Override
	public String execute(String commandName, String locator, String inputValue) {
		try {
			Method method = selenium.getClass().getMethod(commandName, new Class[]{String.class, String.class});
			System.out.println("Invoking: " + method);
			return method.invoke(selenium, new Object[]{locator, inputValue}) + "";
		} catch (Exception e) {
			throw new ExporterException(e);
		}
	}

	@Override
	public String execute(String commandName, String locator) {
		try {
			Method method = selenium.getClass().getMethod(commandName, new Class[]{String.class});
			System.out.println("Invoking: " + method);
			return method.invoke(selenium, new Object[]{locator}) + "";
		} catch (Exception e) {
			throw new ExporterException(e);
		}
	}

	@Override
	public String getText(String locator) {
		return selenium.getText(locator);
	}

	@Override
	public String getTitle() {
		return selenium.getTitle();
	}

	@Override
	public String getValue(String locator) {
		return selenium.getValue(locator);
	}

	@Override
	public boolean isTextPresent(String text) {
		return selenium.isTextPresent(text);
	}

	@Override
	public void waitForPageToLoad(String string) {
		selenium.waitForPageToLoad(string);
	}

	@Override
	public void open(String beginAt) {
		selenium.open(beginAt);
	}

	@Override
	public void setTimeout(String string) {
		selenium.setTimeout(string);
	}

	@Override
	public void start() {
		selenium.start();		
	}

	@Override
	public void stop() {
		selenium.stop();
	}
}
