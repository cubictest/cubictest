package org.cubictest.ui.wizards;

public class WizardNewPropertiesCreationPage extends WizardNewFileCreationPage {

	protected WizardNewPropertiesCreationPage(String pageName) {
		super(pageName);
	}

	@Override
	protected String getFileExtention() {
		return ".properties";
	}

}
