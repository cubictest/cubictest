/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.AbstractNodeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;


/**
 * @author Stein Kåre Skytteren
 *
 */
public class PageNodeEditPolicy extends AbstractNodeEditPolicy {
	
	/*
	 * @see GraphicalNodeEditPolicy#getConnectionCompleteCommand(CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request){
		Transition transition = (Transition)request.getNewObject();
		Test test = (Test)getHost().getParent().getModel();
		CreateTransitionCommand cmd = (CreateTransitionCommand) request.getStartCommand();
		AbstractNodeEditPart part = (AbstractNodeEditPart) request.getTargetEditPart();
		cmd.setTransition(transition);
		cmd.setTarget((TransitionNode)part.getModel());
		cmd.setTest(test);
		return cmd;
	}
}
