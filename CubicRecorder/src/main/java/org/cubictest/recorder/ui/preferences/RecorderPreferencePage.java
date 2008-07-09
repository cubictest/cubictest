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
package org.cubictest.recorder.ui.preferences;

import org.cubictest.exporters.selenium.common.SeleniumPreferencePage;
import org.cubictest.recorder.RecorderPlugin;
import org.cubictest.recorder.ui.RecordEditorActionTarget;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Preference page for the Selenium exporter plugin.
 *
 * @author Christian Schwarz
 */
public class RecorderPreferencePage extends SeleniumPreferencePage implements IWorkbenchPreferencePage {


	@Override
	public String getRememberSettingsKey() {
		return RecordEditorActionTarget.RECORDER_REMEMBER_SETTINGS;
	}

	@Override
	public AbstractUIPlugin getPlugin() {
		return RecorderPlugin.getDefault();
	}
	
}