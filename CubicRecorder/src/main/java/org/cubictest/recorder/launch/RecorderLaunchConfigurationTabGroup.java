package org.cubictest.recorder.launch;

import org.cubictest.exporters.selenium.launch.RunnerLaunchConfigurationTabGroup;
import org.cubictest.exporters.selenium.launch.SeleniumRunnerTab;

public class RecorderLaunchConfigurationTabGroup extends RunnerLaunchConfigurationTabGroup {

	@Override
	protected SeleniumRunnerTab getMainTab() {
		return new SeleniumRecorderTab();
	}
}
