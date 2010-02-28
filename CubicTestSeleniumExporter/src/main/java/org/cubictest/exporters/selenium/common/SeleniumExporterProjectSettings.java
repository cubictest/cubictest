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

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;

public class SeleniumExporterProjectSettings {

	private static final String PLUGIN_PROPERTY_PREFIX = SeleniumUtils.getPluginPropertyPrefix();
	public static final BrowserType DEFAULT_BROWSER = BrowserType.FIREFOX;

	public static BrowserType getPreferredBrowser(CubicTestProjectSettings settings) {
		return BrowserType.fromId(settings.getString(PLUGIN_PROPERTY_PREFIX, "defaultBrowserType", 
				DEFAULT_BROWSER.getId()));
	}
}
