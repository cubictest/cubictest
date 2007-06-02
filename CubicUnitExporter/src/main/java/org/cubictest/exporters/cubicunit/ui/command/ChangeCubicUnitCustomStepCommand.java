package org.cubictest.exporters.cubicunit.ui.command;

import org.cubictest.model.customstep.data.CustomTestStepData;
import org.eclipse.gef.commands.Command;

public class ChangeCubicUnitCustomStepCommand extends Command {

	private CustomTestStepData customTestStepData;
	private String oldDisplayText;
	private String path;
	private String displayText;
	private String oldPath;

	public void setCustomTestStepData(CustomTestStepData customTestStepData) {
		this.customTestStepData = customTestStepData;
	}

	@Override
	public void execute() {
		super.execute();
		oldDisplayText = customTestStepData.getDisplayText();
		customTestStepData.setDisplayText(displayText);
		oldPath = customTestStepData.getPath();
		customTestStepData.setPath(path);
	}
	
	@Override
	public void undo() {
		super.undo();
		customTestStepData.setDisplayText(oldDisplayText);
		customTestStepData.setPath(oldPath);
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}
