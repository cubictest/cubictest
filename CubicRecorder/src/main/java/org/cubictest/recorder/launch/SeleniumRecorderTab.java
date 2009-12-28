package org.cubictest.recorder.launch;

import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.exporters.selenium.launch.SeleniumRunnerTab;

public class SeleniumRecorderTab extends SeleniumRunnerTab {

	@Override
	protected BrowserType[] getSupportedBrowsers() {
		return new BrowserType[] { BrowserType.FIREFOX, BrowserType.OPERA};
	}
}
