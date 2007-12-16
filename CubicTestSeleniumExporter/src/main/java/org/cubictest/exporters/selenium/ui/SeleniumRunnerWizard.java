package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.jface.wizard.Wizard;

public class SeleniumRunnerWizard extends Wizard {
	
	private SeleniumSettingsPage page;
	private CubicTestProjectSettings settings;

	public SeleniumRunnerWizard(CubicTestProjectSettings settings) {
		super();
		setWindowTitle("CubicTest Selenium Runner");
		setNeedsProgressMonitor(true);
		this.settings = settings;
	}
	
	@Override
	public void addPages() {
		page = new SeleniumSettingsPage(settings);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	public int getPort(){
		return page.getPort();
	}
	
	public BrowserType getBrowserType(){
		return page.getBrowserType();
	}
}
