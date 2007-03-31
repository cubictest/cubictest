package org.cubictest.ui.gef.command;

import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;

public class UpdateLangagesCommand extends Command {

	private Test test;

	public void addTest(Test test) {
		this.test = test;
	}
	
	@Override
	public void execute() {
		test.getAllLanguages().updateAllLanguages();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}
	
}
