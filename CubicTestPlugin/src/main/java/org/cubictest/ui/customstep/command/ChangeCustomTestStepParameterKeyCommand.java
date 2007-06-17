package org.cubictest.ui.customstep.command;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.eclipse.gef.commands.Command;

public class ChangeCustomTestStepParameterKeyCommand extends Command {

	private CustomTestStepParameter parameter;
	private String newKey;
	private String oldKey;

	public void setParameter(CustomTestStepParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public void execute() {
		oldKey = parameter.getKey();
		parameter.setKey(newKey);
	}
	
	@Override
	public void undo() {
		parameter.setKey(oldKey);
	}

	public void setNewKey(String newKey) {
		this.newKey = newKey;
	}
}
