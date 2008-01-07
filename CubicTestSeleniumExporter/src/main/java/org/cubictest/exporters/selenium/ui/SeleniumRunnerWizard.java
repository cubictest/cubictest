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
package org.cubictest.exporters.selenium.ui;

import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.jface.wizard.Wizard;

public class SeleniumRunnerWizard extends Wizard {
	
	private SeleniumSettingsPage page;
	private BrowserType browserType;

	public SeleniumRunnerWizard(BrowserType browserType) {
		super();
		setWindowTitle("CubicTest Selenium Runner");
		setNeedsProgressMonitor(true);
		this.browserType = browserType;
	}
	
	@Override
	public void addPages() {
		page = new SeleniumSettingsPage(browserType);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		SeleniumExporterPlugin.getDefault().getDialogSettings().put(
				RunSeleniumRunnerAction.SELENIUM_RUNNER_REMEMBER_SETTINGS, 
				page.shouldRememberSettings() + "");
		return true;
	}
	
	public BrowserType getBrowserType(){
		return page.getBrowserType();
	}
}
