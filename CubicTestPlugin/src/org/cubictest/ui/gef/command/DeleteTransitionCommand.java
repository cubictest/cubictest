/*
 * Created on 06.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kåre Skytteren
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
