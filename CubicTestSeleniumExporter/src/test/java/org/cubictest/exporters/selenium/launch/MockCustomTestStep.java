/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.launch;

import java.util.Map;

import org.cubictest.selenium.custom.ICustomTestStep;
import org.cubictest.selenium.custom.IElementContext;

import com.thoughtworks.selenium.Selenium;

public class MockCustomTestStep implements ICustomTestStep {
	
	public void execute(Map<String, String> arguments, IElementContext context,
			Selenium selenium) {
		if(arguments.size() == 0)
			selenium.fireEvent("locator", "event");
		else{
			String locator = (String)arguments.keySet().toArray()[0];
			selenium.fireEvent(locator, arguments.get(locator));
		}
		String[] results = selenium.getAllFields();
		if(results.length != 3){
			throw new IllegalArgumentException();
		}
		throw new RuntimeException();
	}

}