package org.cubictest.exporters.cubicunit.ui;

import org.eclipse.jface.wizard.Wizard;

public class CubicUnitRunnerWizard extends Wizard {
	
	private SetValuesPage page;

	public CubicUnitRunnerWizard() {
		super();
		setWindowTitle("CubicUnitRunner");
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page = new SetValuesPage();
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
