package org.cubictest.ui.wizards;


public class WizardNewCustomTestStepPage extends WizardNewFileCreationPage {

	public WizardNewCustomTestStepPage() {
		super("New Custom Test Step File");
	}

	@Override
	protected String getFileExtention() {
		return "custom";
	}


}
