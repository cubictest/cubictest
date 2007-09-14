package org.cubictest.exporters.watir;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Bundle activator for plugin.
 * @author Christian Schwarz
 */
public class WatirExporterPlugin extends AbstractUIPlugin {

	private static WatirExporterPlugin plugin;

	public WatirExporterPlugin() {
		plugin = this;
	}

	public static WatirExporterPlugin getDefault(){
		return plugin;
	}
	
}
