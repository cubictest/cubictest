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

import org.cubictest.model.ConnectionPoint;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;


/**
 * Base controller class for start points.
 * 
 * @author Christian Schwarz
 */
public abstract class AbstractStartPointEditPart extends AbstractNodeEditPart {

	/**
	 * Constructor for <code>StartPointEditPart</code>.
	 * @param point the model
	 */
	public AbstractStartPointEditPart(ConnectionPoint point) {
		setModel(point);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected abstract IFigure createFigure();
	

	protected String getName(){
		String name = ((ConnectionPoint)getModel()).getName();
		return name;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		ConnectionPoint connectionPoint = (ConnectionPoint)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		figure.setText(" " + getName() + " ");
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		((TestEditPart)getParent()).setLayoutConstraint(this, figure, r);
	}

	public void updateParams() {
		refresh();
		refreshVisuals();
	}
	
	@Override
	public boolean isCopyable() {
		return false;
	}
	
	@Override
	public boolean isCuttable() {
		return false;
	}
	
}