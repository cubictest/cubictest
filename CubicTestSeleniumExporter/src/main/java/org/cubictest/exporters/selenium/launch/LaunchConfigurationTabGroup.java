package org.cubictest.exporters.selenium.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaConnectTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class LaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup{

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		// TODO Auto-generated method stub
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new SeleniumRunnerTab(),
				new JavaArgumentsTab(),
				new JavaJRETab(),
				new JavaClasspathTab(), 
				//new JavaConnectTab(),
				new CommonTab(),
				new EnvironmentTab()
		};
		setTabs(tabs);
	}

}
