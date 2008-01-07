package org.cubictest.exporters.selenium.ui;

import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.jface.wizard.Wizard;

public class SeleniumRunnerWizard extends Wizard {
	
	private SeleniumSettingsPage page;
	private BrowserType browserType;

	public SeleniumRunnerWizard(BrowserType browserType) {
		super();
		setWindowTitle("CubicTest Selenium Runner");
		setNeedsProgressMonitor(true);
		this.browserType = browserType;
	}
	
	@Override
	public void addPages() {
		page = new SeleniumSettingsPage(browserType);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		SeleniumExporterPlugin.getDefault().getDialogSettings().put(
				RunSeleniumRunnerAction.SELENIUM_RUNNER_REMEMBER_SETTINGS, 
				page.shouldRememberSettings() + "");
		return true;
	}
	
	public BrowserType getBrowserType(){
		return page.getBrowserType();
	}
}
