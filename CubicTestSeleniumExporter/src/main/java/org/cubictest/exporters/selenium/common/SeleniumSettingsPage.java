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

import static org.cubictest.exporters.selenium.common.BrowserType.FIREFOX;
import static org.cubictest.exporters.selenium.common.BrowserType.OPERA;

import org.apache.commons.lang.ArrayUtils;
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
	private Label recorderInfoLabel;;
	private Button rememberSettingsCheckbox;
	private Label rememberSettingsInfoLabel;
	private BrowserType browserType;
	private final boolean recorderMode;
	
	protected SeleniumSettingsPage(BrowserType browserType, boolean recorderMode) {
		super("Choose browser");
		this.browserType = browserType;
		this.recorderMode = recorderMode;
	}

	public void createControl(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		browserLabel = new Label(content, SWT.NONE);
		browserLabel.setText("Browser");
		createBrowserCombo(content);
		
		createRememberSettingsCheckbox(content);
		createRecorderInfo(content);
		
		content.setLayout(gridLayout);
		setMessage("Choose browser to use for " + (recorderMode ? "recording" : "test runner"));
		setPageComplete(true);
		
		setControl(content);
		
		getShell().getDefaultButton().setFocus();

	}
	
	
	private void createRememberSettingsCheckbox(Composite composite) {
		rememberSettingsLabel = new Label(composite, SWT.NONE);
		rememberSettingsLabel.setText("Remember browser");
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

	private void createRecorderInfo(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		recorderInfoLabel = new Label(composite, SWT.NONE);
		recorderInfoLabel.setText("Recorder context menu in Opera:\nTo show context menu, hold \"ctrl\" key and press left mouse button on web page.");
		recorderInfoLabel.setVisible(recorderMode && (browserType == BrowserType.OPERA));
		recorderInfoLabel.setLayoutData(data);
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
	 */
	private void createBrowserCombo(Composite content) {
		browserCombo = new Combo(content, SWT.NONE | SWT.READ_ONLY);
		if (recorderMode) {
			browserCombo.add(FIREFOX.getDisplayName());
			browserCombo.add(OPERA.getDisplayName());
		}
		else {
			for (BrowserType browserType : BrowserType.values()) {
				browserCombo.add(browserType.getDisplayName());
			}
		}
	
		browserCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				browserType = BrowserType.values()[browserCombo.getSelectionIndex()];
				if (recorderInfoLabel != null) {
					if (browserType == BrowserType.OPERA && recorderMode) {
						recorderInfoLabel.setVisible(true);
					}
					else {
						recorderInfoLabel.setVisible(false);
					}
				}
			}
		});
		int storedBrowserTypeIndex = ArrayUtils.indexOf(BrowserType.values(), browserType);
		browserCombo.select(storedBrowserTypeIndex);
	}

	public int getSelectedBrowserIndex() {
		return browserCombo.getSelectionIndex();
	}
}
