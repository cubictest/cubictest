/*
 * Created on 19.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kare Skytteren
 *
 * A command that moves a <code>PageElement</code> within an <code>AbstractPage</code>.
 */
public class MovePageElementCommand extends Command {

	private PageElement child;
	private IContext parent;
	private int oldIndex;
	private int newIndex;

	/**
	 * @param childModel
	 */
	public void setChild(PageElement child) {
		this.child = child;
	}

	/**
	 * @param parent
	 */
	public void setParent(IContext parent) {
		this.parent = parent;
	}

	/**
	 * @param oldIndex
	 */
	public void setOldIndex(int oldIndex) {
		this.oldIndex = oldIndex;
	}

	/**
	 * @param newIndex
	 */
	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		parent.switchPageElement(child,newIndex);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		parent.switchPageElement(child,oldIndex);
	}

}
