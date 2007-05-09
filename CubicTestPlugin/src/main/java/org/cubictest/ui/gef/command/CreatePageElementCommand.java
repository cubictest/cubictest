/*
 * Created on 05.may.2005
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
 * Add a new <code>PageElement</code> to an <code>IContext</code>.
 * 
 * @author Stein Kåre Skytteren
 */
public class CreatePageElementCommand extends Command {

	private IContext context;
	private PageElement pageElement;
	private int index;
	private boolean indexSet = false;
	
	/**
	 * @param context
	 */
	public void setContext(IContext context) {
		this.context = context;
	}
	
	/**
	 * @param pageElement
	 */
	public void setPageElement(PageElement pageElement) {
		this.pageElement = pageElement;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		if (!indexSet) {
			index = context.getElements().size();
		}
		context.addElement(pageElement, index);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo(){
		context.removeElement(pageElement);
	}
	
	public PageElement getPageElement() {
		return pageElement;
	}

	public void setIndex(int index) {
		this.index = index;
		indexSet = true;
	}

}
