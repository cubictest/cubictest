/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.wizards;

import org.cubictest.model.CustomTestStep;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author SK Skytteren Wizard for creating a new <code>FormTransition</code>.
 */
public class NewCustomTestStepWizard extends Wizard {

	private WizardNewCustomTestStepCreationPage newCustomTestStepCreationPage;
	private CustomTestStep customTestStep;

	/**
	 * @param sourceNode
	 */
	public NewCustomTestStepWizard(CustomTestStep customTestStep) {
		this.customTestStep = customTestStep;
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		newCustomTestStepCreationPage = new WizardNewCustomTestStepCreationPage();
		addPage(newCustomTestStepCreationPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		customTestStep.setCustomTestClass(newCustomTestStepCreationPage.getText());
		
		return true;
	}

	public boolean canFinish() {
		if (!newCustomTestStepCreationPage.getText().equals("")) {
			return true;
		}
		return false;
	}
}
