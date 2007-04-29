/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;


/**
 * Contoller for the <code>ExtensionPoint</code> model.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 */
public class ExtensionPointEditPart extends AbstractNodeEditPart {

	private boolean listenerAdded;
	private ExtensionPoint point;

	/**
	 * Constructor for <code>ExtensionPointEditPart</code>.
	 * @param point the model
	 */
	public ExtensionPointEditPart(ExtensionPoint p) {
		this.point = p;
		setModel(point);
		
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		refresh();
		refreshVisuals();
	}

	
	@Override
	public void activate() {
		super.activate();
		if (!listenerAdded) {
			addPageListener();
		}
	}

	
	@Override
	public void deactivate() {
		super.deactivate();
		if (listenerAdded) {
			removePageListener();
		}
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		String name = ((ExtensionPoint)getModel()).getName();
		AbstractTransitionNodeFigure extensionPointFigure = new AbstractTransitionNodeFigure();
		extensionPointFigure.setBackgroundColor(ColorConstants.orange);
		Point p = ((TransitionNode)getModel()).getPosition();
		extensionPointFigure.setLocation(p);
		extensionPointFigure.setText(name);
		extensionPointFigure.setToolTipText("Extension point: $labelText\nOther tests can start from this point.");
		return extensionPointFigure;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		ConnectionPoint connectionPoint = (ConnectionPoint)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		figure.setText(((ExtensionPoint)getModel()).getName());
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
	}

	public void updateParams() {
		refresh();
		refreshVisuals();
	}

	
	private void addPageListener() {
		if (point.getPage() != null) {
			point.getPage().addPropertyChangeListener(this);
			listenerAdded = true;
		}
	}
	
	private void removePageListener() {
		if (point.getPage() != null) {
			point.getPage().removePropertyChangeListener(this);
			listenerAdded = false;
		}
	}
}