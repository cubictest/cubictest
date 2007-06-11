/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import org.cubictest.model.Test;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.ui.INewWizard;


/**
 * Wizard for creating new test suites.
 * 
 * @author Christian Schwarz 
 */

public class NewTestSuiteWizard extends AbstractNewSimpleStartPointTestWizard implements INewWizard {

	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		testDetailsPage = new TestDetailsPage(selection, !extensionPointMap.isEmpty(), "test suite");
		addPage(testDetailsPage);
	}
	
	public Test createEmptyTest(String name, String description) {
		Test emptyTest = WizardUtils.createEmptyTestWithTestSuiteStartPoint("test" + System.currentTimeMillis(), name, description);
		return emptyTest;
	}

	protected void getWizardTitle() {
		setWindowTitle("New CubicTest test suite");
	}

}