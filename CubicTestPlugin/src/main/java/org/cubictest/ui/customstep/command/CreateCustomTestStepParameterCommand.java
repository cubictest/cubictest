package org.cubictest.ui.customstep.command;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepParameterList;
import org.eclipse.gef.commands.Command;

public class CreateCustomTestStepParameterCommand extends Command {

	private CustomTestStepParameterList parameters;
	private CustomTestStepParameter parameter;

	public void setParameters(CustomTestStepParameterList parameters) {
		this.parameters = parameters;
	}

	@Override
	public void execute() {
		parameter = parameters.createNewParameter();
	}
	
	@Override
	public void undo() {
		parameters.remove(parameter);
	}
	
	@Override
	public void redo() {
		parameters.add(parameter);
	}
}
