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
package org.cubictest.exporters.selenium.common;


/**
 * Supported browsers.
 * 
 * @author Christian Schwarz
 */
public enum BrowserType {

	FIREFOX("*firefox", "Firefox", false),
	OPERA("*opera", "Opera", false),
	GOOGLE_CHROME("*googlechrome", "Google Chrome", false),
	INTERNET_EXPLORER("*iexplore", "Internet Explorer", false),
	SAFARI("*safari", "Safari", false),
	FIREFOX_PI("*pifirefox", "Firefox - Proxy injection mode", true),
	INTERNET_EXPLORER_PI("*piiexplore", "Internet Explorer - Proxy injection mode", true);
	
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
		
		if ("*chrome".equals(id)) {
			return FIREFOX;
		}
		else if ("*iehta".equals(id)) {
			return INTERNET_EXPLORER;
		}
		throw new IllegalArgumentException("Unknown BrowserType: " + id);
	}

	public boolean isProxyInjectionMode() {
		return isProxyInjectionMode;
	}
}
