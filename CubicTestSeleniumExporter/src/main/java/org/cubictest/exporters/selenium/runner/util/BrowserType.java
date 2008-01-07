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
package org.cubictest.exporters.selenium.runner.util;


/**
 * Enum for supported browsers.
 * 
 * @author Christian Schwarz
 */
public enum BrowserType {

	FIREFOX("*firefox", "Firefox - standard"),
	//FIREFOX_PI("*pifirefox", "Firefox - proxy injection mode (possibly better frames support)"),
	CHROME("*chrome", "Firefox - chrome version (experimental, better HTTPS support)"),
	INTERNET_EXPLORER("*iexplore", "Internet Explorer - standard"),
	INTERNET_EXPLORER_HTA("*iehta", "Internet Explorer - HTA version (experimental, better HTTPS support)"),
	//INTERNET_EXPLORER_PI("*piiexplore", "Internet Explorer - proxy injection mode"),
	OPERA("*opera", "Opera"),
	SAFARI("*safari", "Safari");
	
	private String id;
	private String displayName;
	
	private BrowserType(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static BrowserType fromId(String id) {
		for (BrowserType browserType : values()) {
			if (browserType.getId().equals(id)) {
				return browserType;
			}
		}
		throw new IllegalArgumentException("Unknown BrowserType: " + id);
	}
}
