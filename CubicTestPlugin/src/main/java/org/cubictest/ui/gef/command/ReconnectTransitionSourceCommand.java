/*
 * Created on 06.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.utils.ModelUtil;
import org.eclipse.gef.commands.Command;


/**
 * Command for reconnection transition source.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class ReconnectTransitionSourceCommand extends Command {

	private Transition transition;
	private TransitionNode newSource;
	private TransitionNode oldSource;

	public ReconnectTransitionSourceCommand(TransitionNode newSource, Transition transition) {
		super();
		this.newSource = newSource;
		this.transition = transition;
		this.oldSource = transition.getStart();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();
		transition.disconnect();
		oldSource.removeOutTransition(transition);
		transition.setStart(newSource);
		transition.connect();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		transition.disconnect();
		newSource.removeOutTransition(transition);
		transition.setStart(oldSource);
		transition.connect();
	}
	
	/*
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (transition instanceof ExtensionTransition) {
			return false;
		}
		return (ModelUtil.isLegalTransition(newSource, transition.getEnd(), true) == ModelUtil.TRANSITION_EDIT_VALID);
	}
}
