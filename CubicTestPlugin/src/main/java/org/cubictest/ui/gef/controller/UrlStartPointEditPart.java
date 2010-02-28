/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
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
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;


/**
 * Contoller for the <code>UrlStartPoint</code> model.
 * 
 * @author SK Skytteren
 *
 */
public class UrlStartPointEditPart extends AbstractStartPointEditPart {

	
	/**
	 * Constructor for <code>UrlStartPointEditPart</code>.
	 * @param point the model
	 */
	public UrlStartPointEditPart(ConnectionPoint point) {
		super(point);
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
		startPointFigure.setToolTipText("Begin at: $labelText");
		return startPointFigure;
	}

	@Override
	protected String getName(){
		String name = ((ConnectionPoint)getModel()).getName();
		if (getModel() instanceof UrlStartPoint){
			name = name + ": " + ((UrlStartPoint)getModel()).getBeginAt();
		}
		return name;
	}

	
}