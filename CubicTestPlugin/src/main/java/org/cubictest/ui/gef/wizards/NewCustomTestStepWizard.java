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
package org.cubictest.ui.gef.wizards;

import org.cubictest.model.CustomTestStepHolder;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author SK Skytteren Wizard for creating a new <code>FormTransition</code>.
 */
public class NewCustomTestStepWizard extends Wizard {

	private WizardNewCustomTestStepCreationPage newCustomTestStepCreationPage;
	private CustomTestStepHolder customTestStep;

	/**
	 * @param sourceNode
	 */
	public NewCustomTestStepWizard(CustomTestStepHolder customTestStep) {
		this.customTestStep = customTestStep;
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		newCustomTestStepCreationPage = new WizardNewCustomTestStepCreationPage();
		addPage(newCustomTestStepCreationPage);
	}

	@Override
	public boolean performFinish() {
		customTestStep.setFile(newCustomTestStepCreationPage.getText());
		
		return true;
	}

	@Override
	public boolean canFinish() {
		if (!newCustomTestStepCreationPage.getText().equals("")) {
			return true;
		}
		return false;
	}
}
