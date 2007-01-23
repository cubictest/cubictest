/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;


/**
 * @author SK Skytteren
 * Contoller for the <code>ConnectionPoint</code> model.
 *
 */
public class UrlStartPointEditPart extends AbstractNodeEditPart {

	/**
	 * Constructor for <code>StartPointEditPart</code>.
	 * @param point the model
	 */
	public UrlStartPointEditPart(ConnectionPoint point) {
		setModel(point);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		
		AbstractTransitionNodeFigure startPointFigure = new AbstractTransitionNodeFigure();
		startPointFigure.setBackgroundColor(ColorConstants.button);
		startPointFigure.setLabelLength(200);
		Point p = ((TransitionNode)getModel()).getPosition();
		startPointFigure.setLocation(p);
		startPointFigure.setText(getName());
		startPointFigure.setToolTipText("Begin at URL: " + getName());
		return startPointFigure;
	}

	private String getName(){
		String name = ((ConnectionPoint)getModel()).getName();
		if (getModel() instanceof UrlStartPoint){
			name = name + ": " + ((UrlStartPoint)getModel()).getBeginAt();
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		ConnectionPoint connectionPoint = (ConnectionPoint)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		figure.setText(getName());
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
	}

	public void updateParams() {
		refresh();
		refreshVisuals();
		//refreshChildren();
	}
}