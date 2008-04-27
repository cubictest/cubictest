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


/**
 * @author Stein K. Skytteren
 *
 *
 * A command that deletes an <code>AbstractPage</code>.
 */
public class DeleteAbstractPageCommand extends DeleteTransitionNodeCommand {

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		test.removePage((AbstractPage) transitionNode);
	}
		
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.addPage((AbstractPage) transitionNode);
	}
	
	public void redo() {
		super.redo();
		test.removePage((AbstractPage) transitionNode);
	}
}
