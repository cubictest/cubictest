package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIndentifierUseParamCommand extends Command {

	private boolean newUseParam;
	private boolean oldUseParam;
	private Identifier identifier;

	public void setNewUseParam(boolean newUseParam) {
		this.newUseParam = newUseParam;	
	}

	public void setOldUseParam(boolean oldUseParam) {
		this.oldUseParam = oldUseParam;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	@Override
	public void execute() {
		super.execute();
		identifier.setUseParam(newUseParam);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setUseParam(oldUseParam);
	}
}
