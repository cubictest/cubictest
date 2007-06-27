package org.cubictest.exporters.selenium;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SeleniumExporterPlugin extends AbstractUIPlugin {

	private static SeleniumExporterPlugin plugin;

	public SeleniumExporterPlugin() {
		plugin = this;
	}

	public static SeleniumExporterPlugin getDefault(){
		return plugin;
	}
	
	public static String getName() {
		return SeleniumExporterPlugin.class.getSimpleName();
	}
}
