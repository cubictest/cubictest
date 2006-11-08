/*
 * Created on 24.may.2005
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
 * @author Stein Kåre Skytteren
 *
 * A command that changes a <code>Form</code>'s name.
 */
public class DeletePageElementCommand extends Command {

	private IContext context;
	private PageElement element;
	private int index;

	/**
	 * @param context
	 */
	public void setContext(IContext context) {
		this.context = context;
	}

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
		index = context.getElementIndex(element);
		context.removeElement(element);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		context.addElement(element, index);
	}
}