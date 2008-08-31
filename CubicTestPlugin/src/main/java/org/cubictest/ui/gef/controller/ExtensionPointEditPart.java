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
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
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
import org.eclipse.swt.graphics.Color;


/**
 * Controller for the <code>ExtensionPoint</code> model.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 */
public class ExtensionPointEditPart extends AbstractNodeEditPart {

	private ExtensionPoint point;

	/**
	 * Constructor for <code>ExtensionPointEditPart</code>.
	 * @param point the model
	 */
	public ExtensionPointEditPart(ExtensionPoint p) {
		this.point = p;
		setModel(point);
		
	}
	
	@Override
	public void activate() {
		super.activate();
		if (point.getPage() != null) {
			point.getPage().addPropertyChangeListener(this);
		}
	}

	
	@Override
	public void deactivate() {
		super.deactivate();
		if (point.getPage() != null) {
			point.getPage().removePropertyChangeListener(this);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt){
		String property = evt.getPropertyName();
		if (evt.getSource().equals(point.getPage())) {
			if (PropertyAwareObject.NAME.equals(property)) {
				//refresh name of this extension point
				refresh();
			}
		}
		else {
			//source is self, refresh on all changes
			refresh();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		String name = ((ExtensionPoint)getModel()).getName();
		AbstractTransitionNodeFigure extensionPointFigure = new AbstractTransitionNodeFigure();
		extensionPointFigure.setBackgroundColor(new Color(null, 255, 238, 180));
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
		figure.setText(" " + ((ExtensionPoint)getModel()).getName()+ " ");
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
	}

	public void updateParams() {
		refresh();
		refreshVisuals();
	}

}