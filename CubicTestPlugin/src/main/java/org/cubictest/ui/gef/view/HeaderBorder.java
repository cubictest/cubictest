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
package org.cubictest.ui.gef.view;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;



/**
 * @author SK Skytteren
 *
 */
public class HeaderBorder extends AbstractBorder {

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Border#getInsets(org.eclipse.draw2d.IFigure)
	 */
	public Insets getInsets(IFigure figure) {
		return new Insets(1,0,1,0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
	 */
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		graphics.setForegroundColor(ColorConstants.black);
		graphics.setLineWidth(1);
		Point left = getPaintRectangle(figure, insets).getBottomLeft();
		left.y = left.y -1;
		Point right = getPaintRectangle(figure, insets).getBottomRight();
		right.y = right.y -1;
		graphics.drawLine(right,left);
		
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.draw2d.Border#isOpaque()
	 */
	@Override
	public boolean isOpaque() {
		return true;
	}
	
}
