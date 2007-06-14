/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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
