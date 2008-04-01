package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeFrameTypeCommand extends Command{

	private Identifier identifier;
	private String frame;
	private String oldFrame;

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public void setOldFrame(String oldFrame) {
		this.oldFrame = oldFrame;
	}
	
	@Override
	public void execute() {
		identifier.setValue(frame);
	}
	
	@Override
	public void undo() {
		identifier.setValue(oldFrame);
	}
}
