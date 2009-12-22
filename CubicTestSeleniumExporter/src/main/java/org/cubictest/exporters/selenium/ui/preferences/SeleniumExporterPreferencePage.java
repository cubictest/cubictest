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
package org.cubictest.exporters.selenium.ui.preferences;

import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.common.SeleniumPreferencePage;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Preference page for the Selenium exporter plugin.
 *
 * @author Christian Schwarz
 */
public class SeleniumExporterPreferencePage extends SeleniumPreferencePage implements IWorkbenchPreferencePage {


	@Override
	public String getRememberSettingsKey() {
		return RunSeleniumRunnerAction.SELENIUM_RUNNER_REMEMBER_SETTINGS;
	}

	@Override
	public AbstractUIPlugin getPlugin() {
		return SeleniumExporterPlugin.getDefault();
	}
	
}