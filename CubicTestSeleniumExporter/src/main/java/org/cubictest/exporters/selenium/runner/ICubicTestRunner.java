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
package org.cubictest.exporters.selenium.runner;

public interface ICubicTestRunner {

	String execute(String commandName, String locator, String inputValue);

	String execute(String commandName, String locator);

	String getTitle();

	String getText(String locator);

	String getValue(String locator);

	boolean isTextPresent(String text);

	void waitForPageToLoad(String string);

	void start();

	void setTimeout(String string);

	void open(String beginAt);

	void stop();

}
