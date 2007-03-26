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
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kare Skytteren
 *
 * A command that changes a <code>Form</code>'s name.
 */
public class TransferPageElementCommand extends Command {

	private PageElement child;
	private IContext originalContext;
	private IContext newContext;
	private int oldIndex;
	private int newIndex;

	/**
	 * @param childModel
	 */
	public void setToMoveModel(PageElement child) {
		this.child = child;
	}

	/**
	 * @param originalContext
	 */
	public void setOriginalContext(IContext originalContext) {
		this.originalContext = originalContext;
	}

	/**
	 * @param newContext
	 */
	public void setNewPage(IContext newContext) {
		this.newContext = newContext;
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
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute(){
		if (child instanceof Option && !(newContext instanceof Select)) {
			//do not move Option outside Select
			return false;
		}
		else if (!(child instanceof Option) && newContext instanceof Select) {
			//do not move non-Option inside Select
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		originalContext.removeElement(child);
		newContext.addElement(child,newIndex);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		newContext.removeElement(child);
		originalContext.addElement(child,oldIndex);
	}
}
