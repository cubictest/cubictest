package org.cubictest.ui.customstep.command;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepParameterList;
import org.eclipse.gef.commands.Command;

public class DeleteCustomTestStepParameterCommand extends Command {

	private CustomTestStepParameterList parameters;
	private CustomTestStepParameter parameter;

	public void setParameters(CustomTestStepParameterList parameters) {
		this.parameters = parameters;
	}

	public void setParameter(CustomTestStepParameter parameter) {
		this.parameter = parameter;	
	}
	
	@Override
	public void execute() {
		parameters.remove(parameter);
	}
	
	@Override
	public void undo() {
		parameters.add(parameter);
	}
}
