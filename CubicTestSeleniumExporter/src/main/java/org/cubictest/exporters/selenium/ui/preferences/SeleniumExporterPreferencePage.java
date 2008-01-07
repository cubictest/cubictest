/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui.preferences;

import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
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

/**
 * Preference page for the Selenium exporter plugin.
 *
 * @author Christian Schwarz
 */
public class SeleniumExporterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

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
			String stored = SeleniumExporterPlugin.getDefault().getDialogSettings().get(RunSeleniumRunnerAction.SELENIUM_RUNNER_REMEMBER_SETTINGS);
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
				SeleniumExporterPlugin.getDefault().getDialogSettings().put(RunSeleniumRunnerAction.SELENIUM_RUNNER_REMEMBER_SETTINGS, 
						rememberSettingsCheckbox.getSelection() + "");
			}
		});
	}

}