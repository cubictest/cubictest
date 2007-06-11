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
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;


/**
 * Contoller for the <code>TestSuiteStartPoint</code> model.
 * 
 * @author Christian Schwarz
 */
public class TestSuiteStartPointEditPart extends AbstractStartPointEditPart {

	/**
	 * Constructor for <code>SubTestStartPoint</code>.
	 * @param point the model
	 */
	public TestSuiteStartPointEditPart(ConnectionPoint point) {
		super(point);
	}

	@Override
	protected String getName() {
		return "Test suite start point";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		
		AbstractTransitionNodeFigure startPointFigure = new AbstractTransitionNodeFigure();
		startPointFigure.setBackgroundColor(new Color(null, 238, 234, 222));
		startPointFigure.setLabelLength(200);
		Point p = ((TransitionNode)getModel()).getPosition();
		startPointFigure.setLocation(p);
		startPointFigure.setText(getName());
		startPointFigure.setToolTipText("The start point for the test suite.");
		return startPointFigure;
	}
	
}