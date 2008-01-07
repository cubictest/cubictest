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
package org.cubictest.ui.gef.view;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Locator for both middle placement and offset from arrow.
 * 
 * @author chr_schwarz
 */
public class MidpointOffsetLocator extends ConnectionEndpointLocator {

	private Connection conn;
	
	public MidpointOffsetLocator(Connection c) {
		super(c, false);
		this.conn = c;
		setVDistance(20);
	}
	
	public int getNewUDistance() {
		PointList points = conn.getPoints();
		Point p1 = points.getPoint(0);
		Point p2 = points.getPoint(1);
		Dimension diff = p2.getDifference(p1);
		double half = Math.sqrt(((diff.height * diff.height) + (diff.width * diff.width))) / 7;
		return (int) half;
	}
	
	@Override
	public void relocate(IFigure figure) {
		setUDistance(getNewUDistance());
		super.relocate(figure);
	}

}
