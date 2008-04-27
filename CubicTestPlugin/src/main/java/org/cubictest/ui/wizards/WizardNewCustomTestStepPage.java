package org.cubictest.ui.wizards;


public class WizardNewCustomTestStepPage extends WizardNewFileCreationPage {

	public WizardNewCustomTestStepPage() {
		super("Create a New Custom Test Step File");
	}

	@Override
	protected String getFileExtention() {
		return ".custom";
	}


}
