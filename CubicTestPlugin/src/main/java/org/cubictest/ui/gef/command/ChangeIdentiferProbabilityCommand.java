package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIdentiferProbabilityCommand extends Command{

	private Identifier identifier;
	private int newProbability;
	private int oldProbability;

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewProbability(int probability) {
		this.newProbability = probability;
	}
	
	public void setOldProbability(int probability) {
		this.oldProbability = probability;
	}
	
	@Override
	public void execute() {
		super.execute();
		identifier.setProbability(newProbability);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setProbability(oldProbability);
	}
}
