/*
 * Created on 26.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
 
package org.cubictest.ui.gef.command;

import org.cubictest.model.AbstractPage;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kåre Skytteren
 * A command that changes an <code>Common</code>'s name.
 */
public class ChangeAbstractPageNameCommand extends Command {

	private String name;
	private String oldName;
	private AbstractPage page;

	/**
	 * @param page
	 */
	public void setAbstractPage(AbstractPage page) {
		this.page = page;
	}

	/**
	 * @param name
	 */
	public void setOldName(String name) {
		this.oldName = name;
	}

	/**
	 * @param string
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		page.setName(name);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		page.setName(oldName);
	}

}
