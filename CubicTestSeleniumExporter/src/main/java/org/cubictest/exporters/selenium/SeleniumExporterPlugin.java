package org.cubictest.exporters.selenium;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class SeleniumExporterPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.cubictest.exporters.selenium";
	private static SeleniumExporterPlugin plugin;
	private BundleContext context;

	public SeleniumExporterPlugin() {
		plugin = this;
	}

	public static SeleniumExporterPlugin getDefault(){
		return plugin;
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		super.start(context);
	}
	
	public Bundle[] getBundles(String bundleName, String version) {
		Bundle[] bundles= Platform.getBundles(bundleName, version);
		if (bundles != null)
			return bundles;
		
		// Accessing unresolved bundle
		ServiceReference serviceRef= context.getServiceReference(PackageAdmin.class.getName());
		PackageAdmin admin= (PackageAdmin)context.getService(serviceRef);
		bundles= admin.getBundles(bundleName, version);
		if (bundles != null && bundles.length > 0)
			return bundles;
		return null;
	}
	
}
