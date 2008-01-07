/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
 
package org.cubictest.ui.gef.command;

import org.cubictest.model.AbstractPage;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein K�re Skytteren
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
