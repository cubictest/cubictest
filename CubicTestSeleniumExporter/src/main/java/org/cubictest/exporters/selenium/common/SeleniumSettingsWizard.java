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

import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SeleniumSettingsWizard extends Wizard {
	
	private SeleniumSettingsPage page;
	private BrowserType preselectedBrowserType;
	private String rememberSettingsKey;
	private String preferredBrowserTypeKey;
	private AbstractUIPlugin plugin;
	private final boolean recorderMode;

	public SeleniumSettingsWizard(BrowserType preselectedBrowserType, String rememberSettingsKey, String preferredBrowserTypeKey, AbstractUIPlugin plugin, boolean recorderMode) {
		super();
		this.recorderMode = recorderMode;
		if (preselectedBrowserType == null) {
			preselectedBrowserType = BrowserType.FIREFOX;
		}
		setWindowTitle("CubicTest Selenium Runner");
		setNeedsProgressMonitor(true);
		this.preselectedBrowserType = preselectedBrowserType;
		this.rememberSettingsKey = rememberSettingsKey;
		this.preferredBrowserTypeKey = preferredBrowserTypeKey;
		this.plugin = plugin;
	}
	
	@Override
	public void addPages() {
		page = new SeleniumSettingsPage(preselectedBrowserType, recorderMode);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		plugin.getDialogSettings().put(rememberSettingsKey, page.shouldRememberSettings() + "");
		plugin.getDialogSettings().put(preferredBrowserTypeKey, page.getSelectedBrowserIndex());
		return true;
	}
	
	public BrowserType getBrowserType(){
		if (page == null) {
			return preselectedBrowserType;
		}
		return page.getBrowserType();
	}
}
