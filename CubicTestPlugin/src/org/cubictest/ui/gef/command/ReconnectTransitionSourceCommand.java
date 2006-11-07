/*
 * Created on 06.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kåre Skytteren
 *
 * A command that changes a <code>Form</code>'s name.
 */
public class ReconnectTransitionSourceCommand extends Command {

	private Transition transition;
	private TransitionNode node;
	private TransitionNode oldNode;

	/**
	 * @param transition
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * @param node
	 */
	public void setSource(TransitionNode node) {
		this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		transition.disconnect();
		oldNode = transition.getStart();
		transition.setStart(node);
		node.addOutTransition(transition);
		transition.connect();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		transition.disconnect();
		node.removeOutTransition(transition);
		transition.setStart(oldNode);
		oldNode.addOutTransition(transition);
		transition.connect();
	}
}
