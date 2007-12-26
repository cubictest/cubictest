package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.jface.wizard.Wizard;

public class SeleniumRunnerWizard extends Wizard {
	
	private SeleniumSettingsPage page;
	private CubicTestProjectSettings settings;
	private int port;
	private BrowserType browserType;

	public SeleniumRunnerWizard(CubicTestProjectSettings settings, int port, BrowserType browserType) {
		super();
		setWindowTitle("CubicTest Selenium Runner");
		setNeedsProgressMonitor(true);
		this.settings = settings;
		this.port = port;
		this.browserType = browserType;
	}
	
	@Override
	public void addPages() {
		page = new SeleniumSettingsPage(settings, port, browserType);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		SeleniumExporterPlugin.getDefault().getDialogSettings().put(
				RunSeleniumRunnerAction.SELENIUM_RUNNER_REMEMBER_SETTINGS, 
				page.shouldRememberSettings() + "");
		return true;
	}
	
	public int getPort(){
		return page.getPort();
	}
	
	public BrowserType getBrowserType(){
		return page.getBrowserType();
	}
}
