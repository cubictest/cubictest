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
public class SeleniumRunnerConfiguration  {

	private BrowserType browserType;
	private String seleniumServerHostname;
	private boolean supportXHtmlNamespaces;		
	private String htmlCaptureAndScreenshotsTargetDir;
	private boolean takeScreenshots;
	private boolean captureHtml;
	private boolean shouldStartCubicSeleniumServer = true;
	private RemoteControlConfiguration rcConfiguration;
	
	/**
	 * Default is that CubicTest starts its own Selenium server at localhost and random port.
	 */
	public SeleniumRunnerConfiguration() {
		rcConfiguration = new RemoteControlConfiguration();
		initDefaultPort();
		this.seleniumServerHostname = "localhost";
		rcConfiguration.setMultiWindow(false);
		setBrowser(BrowserType.FIREFOX);
	}

	private void initDefaultPort() {
		int seleniumServerPort = ExportUtils.findAvailablePort();
		rcConfiguration.setPort(seleniumServerPort);
		rcConfiguration.setPortDriversShouldContact(seleniumServerPort);
	}
	
	public void setBrowser(BrowserType browserType) {
		this.browserType = browserType;
		if (browserType.isProxyInjectionMode()) {
			rcConfiguration.setProxyInjectionModeArg(true);
		}
	}
	
	public BrowserType getBrowser() {
		return this.browserType;
	}
	

	public String getSeleniumServerHostname() {
		return seleniumServerHostname;
	}
	
	public int getSeleniumServerPort() {
		return rcConfiguration.getPort();
	}

	
	/**
	 * Set hostname (or IP address) and port of existing Selenium Server to use.
	 * This will prevent CubicTest from starting its own Selenium Server at localhost and random port.
	 */
	public void setUseExistingSeleniumServer(String seleniumServerHostname, int seleniumServerPort) {
		this.seleniumServerHostname = seleniumServerHostname;
		rcConfiguration.setPort(seleniumServerPort);
		rcConfiguration.setPortDriversShouldContact(seleniumServerPort);
		this.shouldStartCubicSeleniumServer = false;
	}
	
	public void setCaptureHtml(boolean captureHtml) {
		this.captureHtml = captureHtml;
	}

	public void setSupportXHtmlNamespaces(boolean useNamespace) {
		this.supportXHtmlNamespaces = useNamespace;
	}

	public void setHtmlCaptureAndScreenshotsTargetDir(String workingDirName) {
		System.out.println("Using working directory: " + workingDirName);
		this.htmlCaptureAndScreenshotsTargetDir = workingDirName;
	}

	public void setTakeScreenshots(boolean takeScreenshots) {
		this.takeScreenshots = takeScreenshots;
	}

	public boolean isSupportXHtmlNamespaces() {
		return supportXHtmlNamespaces;
	}

	public String getHtmlCaptureAndScreenshotsTargetDir() {
		if (isBlank(htmlCaptureAndScreenshotsTargetDir)) {
			setHtmlCaptureAndScreenshotsTargetDir(new File("").getAbsolutePath());
		}
		return htmlCaptureAndScreenshotsTargetDir;
	}

	public boolean isTakeScreenshots() {
		return takeScreenshots;
	}

	public boolean isCaptureHtml() {
		return captureHtml;
	}

	public boolean shouldStartCubicSeleniumServer() {
		return shouldStartCubicSeleniumServer;
	}

	public void setRemoteControlConfiguration(RemoteControlConfiguration rcConfiguration) {
		this.rcConfiguration = rcConfiguration;
	}

	public RemoteControlConfiguration getRemoteControlConfiguration() {
		return rcConfiguration;
	}

	public void setMultiWindow(boolean seleniumMultiWindow) {
		rcConfiguration.setMultiWindow(seleniumMultiWindow);
	}
}
