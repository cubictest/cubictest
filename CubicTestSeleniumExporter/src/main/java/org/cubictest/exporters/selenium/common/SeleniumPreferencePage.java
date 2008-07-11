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
package org.cubictest.exporters.selenium.common;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Preference page for the Selenium exporter plugin.
 *
 * @author Christian Schwarz
 */
public abstract class SeleniumPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Label rememberSettingsLabel;
	private Button rememberSettingsCheckbox;

	
	@Override
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		createRememberSettingsCheckbox(content);
		content.setLayout(gridLayout);
		return content;
	}

	public void init(IWorkbench workbench) {
	}
	
	public boolean getStoredSetting() {
		boolean remember = false;
		try {
			String stored = getPlugin().getDialogSettings().get(getRememberSettingsKey());
			if ("true".equals(stored)) {
				remember = true;
			}
		}
		catch (Exception ignore) {
		}
		return remember;
	}
	
	
	private void createRememberSettingsCheckbox(Composite composite) {
		rememberSettingsLabel = new Label(composite, SWT.NONE);
		rememberSettingsLabel.setText("Remember browser type: ");
		rememberSettingsCheckbox = new Button(composite, SWT.CHECK);
		rememberSettingsCheckbox.setSelection(getStoredSetting());
		rememberSettingsCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				getPlugin().getDialogSettings().put(getRememberSettingsKey(), 
						rememberSettingsCheckbox.getSelection() + "");
			}
		});
	}

	public abstract String getRememberSettingsKey();
	
	public abstract AbstractUIPlugin getPlugin();
}