package org.cubictest.exporters.selenium.launch;

import org.cubictest.model.Test;
import org.eclipse.swt.widgets.Display;

public class RunnerParameters {

	public Test test;
	public Display display;
	public int remoteRunnerClientListenerPort;
	public int seleniumClientProxyPort;

}
