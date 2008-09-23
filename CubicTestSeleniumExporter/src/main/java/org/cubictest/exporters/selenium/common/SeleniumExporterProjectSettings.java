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
