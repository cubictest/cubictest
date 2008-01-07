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
import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;


/**
 * A command for a new <code>AbstractPage</code> in a <code>Test</code>
 *
 * @author SK Skytteren
 */
public class AddAbstractPageCommand extends Command {

	private Test test;
	private AbstractPage page;

	/**
	 * @param test
	 */
	public void setTest(Test test) {
		this.test = test;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		test.addPage(page);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.removePage(page);
	}

	public AbstractPage getPage() {
		return page;
	}

	/**
	 * @param page
	 */
	public void setPage(AbstractPage page) {
		this.page = page;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		test.addPage(page);
	}
}
