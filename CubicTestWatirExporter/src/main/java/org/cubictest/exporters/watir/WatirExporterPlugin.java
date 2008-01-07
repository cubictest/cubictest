/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
