/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
		testDetailsPage.setFileExt(".ats");
		addPage(testDetailsPage);
	}
	@Override
	public Test createEmptyTest(String name, String description) {
		Test emptyTest = WizardUtils.createEmptyTestWithTestSuiteStartPoint(name, description);
		return emptyTest;
	}
	@Override
	protected void getWizardTitle() {
		setWindowTitle("New CubicTest test suite");
	}

}