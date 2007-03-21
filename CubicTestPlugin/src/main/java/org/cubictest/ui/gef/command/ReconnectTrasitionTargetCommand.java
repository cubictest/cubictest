/*
 * Created on 06.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.CommonTransition;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.utils.ModelUtil;
import org.eclipse.gef.commands.Command;


/**
 * A command that reconnect a <code>Transition</code>'s target to another <code>TransitonNode</code>.
 *
 * @author SK Skytteren 
 */
public class ReconnectTrasitionTargetCommand extends Command {

	private Transition transition;
	private TransitionNode newTarget;
	private TransitionNode oldTarget;
	private boolean isNoModelChanges;

	/**
	 * @param transition
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * @param newTarget
	 */
	public void setNewTarget(TransitionNode node) {
		this.newTarget = node;	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();
		if (isNoModelChanges)
			return;
		
		this.oldTarget = transition.getEnd();
		if (transition instanceof CommonTransition){
			((Page)oldTarget).removeCommonTransition((CommonTransition)transition);
			transition.setEnd(newTarget);
			((Page)newTarget).addCommonTransition((CommonTransition) transition);
		}else{
			oldTarget.setInTransition(null);
			transition.setEnd(newTarget);
			newTarget.setInTransition(transition);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		if (isNoModelChanges)
			return;

		if (transition instanceof CommonTransition){
			((Page)newTarget).removeCommonTransition((CommonTransition)transition);
			transition.setEnd(oldTarget);
			((Page)oldTarget).addCommonTransition((CommonTransition) transition);
		}else{
			transition.setEnd(oldTarget);
			oldTarget.setInTransition(transition);
			newTarget.setInTransition(null);
		}
	}
	
	/*
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		int status = ModelUtil.isLegalTransition(transition.getStart(), newTarget, false);
		isNoModelChanges = (status == ModelUtil.TRANSITION_EDIT_NO_CHANGES);
		return (status == ModelUtil.TRANSITION_EDIT_NO_CHANGES || status == ModelUtil.TRANSITION_EDIT_VALID);
	}

}
