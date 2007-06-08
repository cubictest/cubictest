package org.cubictest.exporters.cubicunit;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class CubicUnitRunnerPlugin extends AbstractUIPlugin {

	private static CubicUnitRunnerPlugin plugin;

	public CubicUnitRunnerPlugin() {
		plugin = this;
	}
	
	public static CubicUnitRunnerPlugin getDefault(){
		return plugin;
	}
	
	

}
