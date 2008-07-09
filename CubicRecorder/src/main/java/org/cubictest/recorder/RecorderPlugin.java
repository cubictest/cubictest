/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.recorder;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RecorderPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.cubictest.recorder";
	private static RecorderPlugin plugin;

	public RecorderPlugin() {
		plugin = this;
	}

	public static RecorderPlugin getDefault(){
		return plugin;
	}
	
}
