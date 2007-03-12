package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIdentifierI18nKeyCommand extends Command{

	private Identifier identifier;
	private String newI18nKey;
	private String oldKey;

	public void setIndentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public void setNewI18nKey(String newKey) {
		this.newI18nKey = newKey;
	}

	public void setOldI18nKey(String oldKey) {
		this.oldKey = oldKey;	
	}
	
	@Override
	public void execute() {
		super.execute();
		identifier.setI18nKey(newI18nKey);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setI18nKey(oldKey);
	}
}
