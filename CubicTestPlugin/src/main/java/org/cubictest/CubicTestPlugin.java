/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Activating CubicTest ");
		
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
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
