package org.cubictest.ui.gef.command;

import org.cubictest.model.customstep.CustomTestStepValue;
import org.eclipse.gef.commands.Command;

public class ChangeCustomTestStepValueCommand extends Command{

	private final CustomTestStepValue paramValue;
	private final String text;
	private String oldText;

	public ChangeCustomTestStepValueCommand(CustomTestStepValue paramValue, String text) {
		this.paramValue = paramValue;
		this.text = text;
	}

	@Override
	public void execute() {
		this.oldText = paramValue.getValue();
		paramValue.setValue(text);
	}
	
	@Override
	public void undo() {
		paramValue.setValue(oldText);
	}
	
	@Override
	public void redo() {
		paramValue.setValue(text);
	}
}
