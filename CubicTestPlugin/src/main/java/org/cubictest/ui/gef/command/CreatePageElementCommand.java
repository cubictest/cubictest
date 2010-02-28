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
 * Add a new <code>PageElement</code> to an <code>IContext</code>.
 * 
 * @author Stein K. Skytteren
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
			index = context.getRootElements().size();
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

	public IContext getContext() {
		return context;
	}

}
