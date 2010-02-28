/*******************************************************************************
 * Copyright (c) 2005, 2010 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - enhanced features, bug fixes and usability improvements
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
