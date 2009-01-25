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

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;

import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.openqa.selenium.server.RemoteControlConfiguration;

/**
 * Class for configuring the CubicTest Selenium Runner.
 * Has two functions: Controlling the Selenium Server and controlling the Selenium Server Client Browser (Selenium interface).
 * 
 * Extends the Selenium RC RemoteControlConfiguration, and supports all settings defined there, and defines additional client settings.
 * 
 * @author Christian Schwarz
 */
public class SeleniumRunnerConfiguration extends RemoteControlConfiguration {

	private static final String LOCALHOST = "localhost";
	private BrowserType browserType;
	private String seleniumServerHostname;
	private int seleniumServerPort;
	private boolean useNamespace;		
	private String workingDirName;
	private boolean takeScreenshots;
	private boolean captureHtml;
	private boolean serverAutoHostAndPort = true;
		
	
	public SeleniumRunnerConfiguration() {
		initDefaultSeleniumServerPort();
		this.seleniumServerHostname = LOCALHOST;
		setMultiWindow(false);
		setBrowser(BrowserType.FIREFOX);
	}

	private void initDefaultSeleniumServerPort() {
		int seleniumServerPort = ExportUtils.findAvailablePort();
		setPort(seleniumServerPort);
		setPortDriversShouldContact(seleniumServerPort);
	}
	
	public void setBrowser(BrowserType browserType) {
		this.browserType = browserType;
		if (browserType.isProxyInjectionMode()) {
			setProxyInjectionModeArg(true);
		}
	}
	
	public BrowserType getBrowser() {
		return this.browserType;
	}
	

	public String getSeleniumServerHostname() {
		return seleniumServerHostname;
	}
	
	public int getSeleniumServerPort() {
		return seleniumServerPort;
	}

	
	/**
	 * Set hostname (or IP address) and port of Selenium Server to use.
	 * If not localhost or 127.0.0.1, a local server will not be started.
	 */
	public void setSeleniumServer(String seleniumServerHostname, int seleniumServerPort) {
		this.seleniumServerHostname = seleniumServerHostname;
		this.seleniumServerPort = seleniumServerPort;
		setPort(seleniumServerPort);
		setPortDriversShouldContact(seleniumServerPort);
		this.serverAutoHostAndPort = false;
	}
	
	public void setCaptureHtml(boolean captureHtml) {
		this.captureHtml = captureHtml;
	}

	public void setUseNamespace(boolean useNamespace) {
		this.useNamespace = useNamespace;
	}

	public void setWorkingDirName(String workingDirName) {
		System.out.println("Using working directory: " + workingDirName);
		this.workingDirName = workingDirName;
	}

	public void setTakeScreenshots(boolean takeScreenshots) {
		this.takeScreenshots = takeScreenshots;
	}

	public void setServerAutoHostAndPort(boolean serverAutoHostAndPort) {
		this.serverAutoHostAndPort = serverAutoHostAndPort;
	}
	
	public boolean isUseNamespace() {
		return useNamespace;
	}

	public String getWorkingDirName() {
		if (isBlank(workingDirName)) {
			setWorkingDirName(new File("").getAbsolutePath());
		}
		return workingDirName;
	}

	public boolean isTakeScreenshots() {
		return takeScreenshots;
	}

	public boolean isCaptureHtml() {
		return captureHtml;
	}

	public boolean isServerAutoHostAndPort() {
		return serverAutoHostAndPort;
	}
}
