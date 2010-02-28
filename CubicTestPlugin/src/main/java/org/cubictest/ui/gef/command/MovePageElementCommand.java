/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
	
	@Override
	public boolean canExecute() {
		return true;
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
