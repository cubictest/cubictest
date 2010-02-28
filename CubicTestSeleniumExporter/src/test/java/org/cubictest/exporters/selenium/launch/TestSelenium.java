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

import com.thoughtworks.selenium.DefaultSelenium;

public class TestSelenium extends DefaultSelenium {
	
	private String testLocator;
	private String testEventName;

	public TestSelenium() {
		super(null);
	}

	@Override
	public void fireEvent(String locator, String eventName) {
		testLocator = locator;
		testEventName = eventName;
	}
	
	@Override
	public String[] getAllFields() {
		return new String[]{"en","to","tre"};
	}

	public String getTestLocator() {
		return testLocator;
	}

	public String getTestEventName() {
		return testEventName;
	}

	
}
