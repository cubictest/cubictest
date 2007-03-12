package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIdentifierParamKeyCommand extends Command {

	private Identifier identifier;
	private String newParamKey;
	private String oldParamKey;

	public void setIndentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewParamKey(String newParamKey) {
		this.newParamKey = newParamKey;
	}

	public void setOldParamKey(String oldParamKey) {
		this.oldParamKey = oldParamKey;
	}
	
	@Override
	public void execute() {
		super.execute();
		identifier.setParamKey(newParamKey);
	}
	@Override
	public void undo() {
		super.undo();
		identifier.setParamKey(oldParamKey);
	}
}
