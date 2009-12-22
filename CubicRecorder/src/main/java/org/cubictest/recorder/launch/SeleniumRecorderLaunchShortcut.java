package org.cubictest.recorder.launch;

import org.cubictest.exporters.selenium.launch.AbstractRunnerLaunchShortcut;

public class SeleniumRecorderLaunchShortcut extends AbstractRunnerLaunchShortcut {
	protected String getLaunchConfigurationTypeId() {
		return "org.cubictest.recorder.ui.launchConfigurationType";
	}
}
