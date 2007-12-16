package org.cubictest.exporters.selenium.ui;

import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.jface.wizard.Wizard;

public class SeleniumRunnerWizard extends Wizard {
	
	private SeleniumSettingsPage page;

	public SeleniumRunnerWizard() {
		super();
		setWindowTitle("CubicTest Selenium Runner");
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page = new SeleniumSettingsPage();
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
