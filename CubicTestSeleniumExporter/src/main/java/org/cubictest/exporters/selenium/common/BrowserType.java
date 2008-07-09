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
package org.cubictest.exporters.selenium.common;


/**
 * Enum for supported browsers.
 * 
 * @author Christian Schwarz
 */
public enum BrowserType {

	FIREFOX("*firefox", "Firefox - standard", false),
	CHROME("*chrome", "Firefox - chrome version (experimental, better HTTPS support)", false),
	FIREFOX_PI("*pifirefox", "Firefox - proxy injection mode (experimental)", true),
	OPERA("*opera", "Opera", false),
	INTERNET_EXPLORER("*iexplore", "Internet Explorer - standard", false),
	INTERNET_EXPLORER_HTA("*iehta", "Internet Explorer - HTA version (experimental, better HTTPS support)", false),
	INTERNET_EXPLORER_PI("*piiexplore", "Internet Explorer - proxy injection mode (experimental)", true),
	SAFARI("*safari", "Safari", false);
	
	private String id;
	private String displayName;
	private boolean isProxyInjectionMode;
	
	
	private BrowserType(String id, String displayName, boolean isProxyInjectionMode) {
		this.id = id;
		this.displayName = displayName;
		this.isProxyInjectionMode = isProxyInjectionMode;
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

	public boolean isProxyInjectionMode() {
		return isProxyInjectionMode;
	}
}
