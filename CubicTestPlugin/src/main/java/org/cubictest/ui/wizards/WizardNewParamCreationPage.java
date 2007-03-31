/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;


public class WizardNewParamCreationPage extends WizardNewFileCreationPage{

	protected WizardNewParamCreationPage(String pageName) {
		super(pageName);
	}

	@Override
	protected String getFileExtention() {
		return ".params";
	}
}
