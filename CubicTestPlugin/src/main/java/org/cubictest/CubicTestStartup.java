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

import org.cubictest.ui.eclipse.CubicTestResourceChangeListener;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.ui.IStartup;

public class CubicTestStartup implements IStartup {

	private CubicTestResourceChangeListener listener;
	private static final String SKIP_INTRO = "CubicTestFirstTime";

	public CubicTestStartup() {
		listener = new CubicTestResourceChangeListener();
	}

	public void earlyStartup() {
		Preferences prefs = CubicTestPlugin.getDefault().getPluginPreferences();
		if(!prefs.getBoolean(SKIP_INTRO)){
			prefs.setValue(SKIP_INTRO, true);
		}
		CubicTestPlugin.getDefault().savePluginPreferences();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			 listener, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);
	}

}
