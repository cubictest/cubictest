package org.cubictest.ui.customstep.section;

import org.cubictest.model.customstep.data.CustomTestStepData;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Composite;

public abstract class CustomStepSection {

	public final static int STANDARD_LABEL_WIDTH = 200;
	private CommandStack commandStack;
	
	public abstract void createControl(Composite parent);

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}
	
	public abstract void setData(CustomTestStepData data);
	
	public abstract String getDataKey();
	
}
