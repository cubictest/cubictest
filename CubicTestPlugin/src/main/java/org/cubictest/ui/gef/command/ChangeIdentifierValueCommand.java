package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIdentifierValueCommand extends Command {

	private String newValue;
	private String oldValue;
	private Identifier identifier;
	private boolean oldUseParam;
	private boolean oldUseI18n;

	public void setIdentifer(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;	
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	@Override
	public void execute() {
		super.execute();
		identifier.setValue(newValue);
		oldUseI18n = identifier.useI18n();
		identifier.setUseI18n(false);
		oldUseParam = identifier.useParam();
		identifier.setUseParam(false);
	}
	@Override
	public void undo() {
		super.undo();
		identifier.setValue(oldValue);
		identifier.setUseI18n(oldUseI18n);
		identifier.setUseParam(oldUseParam);
	}
}
