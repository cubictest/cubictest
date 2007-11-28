package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.cubictest.model.Moderator;
import org.eclipse.gef.commands.Command;

public class ChangeIdentiferModeratorCommand extends Command{

	private Moderator newMod;
	private Moderator oldMod;
	private Identifier identifier;

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewModerator(Moderator newMod) {
		this.newMod = newMod;
	}

	public void setOldModerator(Moderator oldMod) {
		this.oldMod = oldMod;
	}

	@Override
	public void execute() {
		identifier.setModerator(newMod);
	}
	
	@Override
	public void undo() {
		identifier.setModerator(oldMod);
	}
}
