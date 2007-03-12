package org.cubictest.ui.gef.command;

import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;

public class ChangePageElementNotCommand extends Command {

	private PageElement pageElement;
	private boolean newNot;
	private boolean oldNot;

	public void setPageElement(PageElement pageElement) {
		this.pageElement = pageElement;
	}

	public void setNewNot(boolean newNot) {
		this.newNot = newNot;
	}

	public void setOldNot(boolean oldNot) {
		this.oldNot = oldNot;
	}
	
	@Override
	public void execute() {
		super.execute();
		pageElement.setNot(newNot);
	}

	@Override
	public void undo() {
		super.undo();
		pageElement.setNot(oldNot);
	}
}
