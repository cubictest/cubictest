/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.MoveNodeCommand;
import org.cubictest.ui.gef.command.PageResizeCommand;
import org.cubictest.ui.gef.controller.AbstractNodeEditPart;
import org.cubictest.ui.gef.controller.ExtensionStartPointEditPart;
import org.cubictest.ui.gef.controller.SubTestStartPointEditPart;
import org.cubictest.ui.gef.controller.UrlStartPointEditPart;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;


/**
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class TestXYLayoutEditPolicy extends XYLayoutEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (!(child instanceof AbstractNodeEditPart))
			return null;
		if (child instanceof UrlStartPointEditPart)
			return null;
		if (child instanceof ExtensionStartPointEditPart)
			return null;
		if (child instanceof SubTestStartPointEditPart)
			return null;
		if (!(constraint instanceof Rectangle))
			return null;

		AbstractNodeEditPart abstractNodeEditPart = (AbstractNodeEditPart) child;
		TransitionNode transitionNode = (TransitionNode)abstractNodeEditPart.getModel();

		Figure figure = (Figure) abstractNodeEditPart.getFigure();
		Rectangle oldBounds = figure.getBounds();
		Rectangle newBounds = (Rectangle) constraint;

		if ((newBounds.width != -1 && newBounds.height != -1) && 
				((oldBounds.width != newBounds.width) || (oldBounds.height != newBounds.height))) {
			PageResizeCommand command = new PageResizeCommand();
			command.setNode(transitionNode);
			command.setOldDimension(new Dimension(oldBounds.width, oldBounds.height));
			command.setNewDimension(new Dimension(newBounds.width, newBounds.height));
			return command;
		}
		else
		{
			MoveNodeCommand command = new MoveNodeCommand();
			command.setNode(transitionNode);
			command.setOldPosition(new Point(oldBounds.x,oldBounds.y));
			command.setNewPosition(new Point(newBounds.x,newBounds.y));
			return command;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	@Override
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

}
