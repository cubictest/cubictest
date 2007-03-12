package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIndentifierUseI18nCommand extends Command {

	private boolean newUseI18n;
	private boolean oldUseI18n;
	private Identifier identifier;
	public void setNewUseI18n(boolean newUseI18n) {
		this.newUseI18n = newUseI18n;
	}
	public void setOldUseI18n(boolean oldUseI18n) {
		this.oldUseI18n = oldUseI18n;
	}
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	@Override
	public void execute() {
		super.execute();
		identifier.setUseI18n(newUseI18n);
	}
	@Override
	public void undo() {
		super.undo();
		identifier.setUseI18n(oldUseI18n);
	}
}
