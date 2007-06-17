package org.cubictest.ui.customstep.command;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.eclipse.gef.commands.Command;

public class ChangeCustomTestStepParameterDescriptionCommand extends Command {

	private CustomTestStepParameter parameter;
	private String newDescription;
	private String oldDescription;

	public void setParameter(CustomTestStepParameter parameter) {
		this.parameter = parameter;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	@Override
	public void execute() {
		oldDescription = parameter.getDescription();
		parameter.setDescription(newDescription);
	}
	
	@Override
	public void undo() {
		parameter.setDescription(oldDescription);
	}
	
	@Override
	public void redo() {
		parameter.setDescription(newDescription);
	}
}
