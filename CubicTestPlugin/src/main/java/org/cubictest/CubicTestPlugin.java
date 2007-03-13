/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */

package org.cubictest;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CubicTestPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static CubicTestPlugin plugin;

	/**
	 * The constructor.
	 */
	public CubicTestPlugin() {

		plugin = this;

	}

	public String getId() {
		return "org.cubictest";
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Activating CubicTest ");
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Deactivating CubicTest ");
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static CubicTestPlugin getDefault() {
		if(plugin == null){
			plugin = new CubicTestPlugin();
		}
		return plugin;
	}

}
