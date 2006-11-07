/*
 * Created on 28.may.2005
 *
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kåre Skytteren
 *
 * A command that changes a <code>PageElement</code>'s present or not state.
 */
public class ChangePresentCommand extends Command {

	private PageElement element;

	/**
	 * @param element
	 */
	public void setPageElement(PageElement element) {
		this.element = element;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (element.isNot()) element.setNot(false);
		else element.setNot(true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		execute();
	}

}
