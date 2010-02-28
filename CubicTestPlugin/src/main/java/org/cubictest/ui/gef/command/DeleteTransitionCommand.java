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

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein K. Skytteren
 *
 * A command that deletes <code>Transition</code>.
 */
public class DeleteTransitionCommand extends Command {

	private Transition transition;
	private Test test;
	
	/**
	 * Constructor for the <code>DeleteTransitionCommand</code>.
	 * 
	 * @param test the <code>Test</code> where the <code>TrasntionNode</code> is located.
	 * @param transition the <code>Test</code> to delete.
	 */
	public DeleteTransitionCommand(Transition transition, Test test) {
		super();
		this.transition = transition;
		this.test = test;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		test.removeTransition(transition);			
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		test.addTransition(transition);			
	}
}
