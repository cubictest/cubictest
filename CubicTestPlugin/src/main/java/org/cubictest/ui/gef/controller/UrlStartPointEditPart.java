/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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