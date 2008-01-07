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
package org.cubictest.exporters.selenium.ui;

import org.apache.commons.lang.ArrayUtils;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Wizard page for setting Selenium Runner options.
 * @author Christian Schwarz
 */
public class SeleniumSettingsPage extends WizardPage {

	private Label browserLabel;
	private Combo browserCombo;
	private Label rememberSettingsLabel;
	private Button rememberSettingsCheckbox;
	private Label rememberSettingsInfoLabel;
	private BrowserType browserType;
	
	protected SeleniumSettingsPage(BrowserType browserType) {
		super("Set CubicSeleniumServerPort");
		this.browserType = browserType;
	}

	public void createControl(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		browserLabel = new Label(content, SWT.NONE);
		browserLabel.setText("Label");
		createBrowserCombo(content);
		
		createRememberSettingsCheckbox(content);
		
		content.setLayout(gridLayout);
		setMessage("Choose Browser.");
		setPageComplete(true);
		
		setControl(content);
	}
	
	
	private void createRememberSettingsCheckbox(Composite composite) {
		rememberSettingsLabel = new Label(composite, SWT.NONE);
		rememberSettingsLabel.setText("Remember browser:");
		rememberSettingsCheckbox = new Button(composite, SWT.CHECK);
		rememberSettingsCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				//store of property handled by wizard after OK is pressed
				rememberSettingsInfoLabel.setVisible(rememberSettingsCheckbox.getSelection());
			}
		});
		GridData data = new GridData();
		data.horizontalSpan = 2;
		rememberSettingsInfoLabel = new Label(composite, SWT.NONE);
		rememberSettingsInfoLabel.setText("Setting can be reset in Window -> Preferences -> CubicTest");
		rememberSettingsInfoLabel.setVisible(false);
		rememberSettingsInfoLabel.setLayoutData(data);
	}
	

	public BrowserType getBrowserType(){
		
		return browserType;
	}
	
	public boolean shouldRememberSettings() {
		return rememberSettingsCheckbox.getSelection();
	}

	/**
	 * This method initializes browserCombo	
	 * @param content 
	 *
	 */
	private void createBrowserCombo(Composite content) {
		browserCombo = new Combo(content, SWT.NONE | SWT.READ_ONLY);
		for (BrowserType browserType : BrowserType.values()) {
			browserCombo.add(browserType.getDisplayName());
		}
	
		browserCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				browserType = BrowserType.values()[browserCombo.getSelectionIndex()];
				SeleniumExporterPlugin.getDefault().getDialogSettings().put(
						RunSeleniumRunnerAction.SELENIUM_RUNNER_BROWSER_TYPE, browserCombo.getSelectionIndex());
			}
		});
		int storedBrowserTypeIndex = ArrayUtils.indexOf(BrowserType.values(), browserType);
		browserCombo.select(storedBrowserTypeIndex);
	}
}
