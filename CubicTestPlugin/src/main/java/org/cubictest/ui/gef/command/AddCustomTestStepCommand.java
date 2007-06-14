/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.wizards.NewCustomTestStepWizard;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class AddCustomTestStepCommand extends Command {

	private Test test;
	private CustomTestStepHolder customTestStep;

	public AddCustomTestStepCommand(Test test, CustomTestStepHolder customTestStep) {
		this.test = test;
		this.customTestStep = customTestStep;
	}
	
	@Override
	public void execute() {
		if(customTestStep.getFilePath() == null) {
			NewCustomTestStepWizard wizard = new NewCustomTestStepWizard(customTestStep);
			WizardDialog dlg = new WizardDialog(new Shell(),
					wizard);
			if (dlg.open() == WizardDialog.CANCEL) {
				return;
			}
		}

		test.addCustomTestStep(customTestStep);
	}

	public void setTest(Test test) {
		this.test = test;
	}


	
	@Override
	public void undo() {
		if(customTestStep != null) {
			test.removeCustomTestSteps(customTestStep);
		}
	}

	@Override
	public void redo() {
		if(customTestStep != null) {
			test.addCustomTestStep(customTestStep);
		}
	}	
}
