package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;

/**
 * Changes the identifier used for direct edit of a page element.
 *  
 * @author Christian Schwarz
 */
public class ChangeDirectEditIdentifierCommand extends Command {

	private Identifier newIdentifier;
	private Identifier oldIdentifier;
	private PageElement pageElement;


	@Override
	public void execute() {
		super.execute();
		pageElement.setDirectEditIdentifier(newIdentifier);
	}
	@Override
	public void undo() {
		super.undo();
		pageElement.setDirectEditIdentifier(oldIdentifier);
	}

	
	public void setNewIdentifier(Identifier newIdentifier) {
		this.newIdentifier = newIdentifier;
	}

	public void setOldIdentifier(Identifier oldIdentifier) {
		this.oldIdentifier = oldIdentifier;
	}

	public void setPageElement(PageElement pageElement) {
		this.pageElement = pageElement;
	}
}
