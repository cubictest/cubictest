package org.cubictest.ui.customstep.command;

import org.cubictest.model.customstep.CustomTestStep;
import org.eclipse.gef.commands.Command;

public class ChangeCustomStepDescriptionCommand extends Command {

	private String description;
	private CustomTestStep customStep;
	private String oldDescription;

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCustomStep(CustomTestStep customStep) {
		this.customStep = customStep;	
	}

	@Override
	public void execute() {
		this.oldDescription = customStep.getDescription();
		customStep.setDescription(description);
	}
	
	@Override
	public void undo() {
		super.undo();
		customStep.setDescription(oldDescription);
	}
}
