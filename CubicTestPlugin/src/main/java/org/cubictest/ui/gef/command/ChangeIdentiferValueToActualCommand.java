package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIdentiferValueToActualCommand extends Command {

	private String value;
	private String actual;
	private Identifier identifier;

	public void setValue(String value) {
		this.value = value;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	@Override
	public void execute() {
		super.execute();
		identifier.setValue(actual);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setValue(value);
	}
}
