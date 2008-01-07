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

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Free form layer for use in scrollbar of abstract page.
 * Overrides add method to keep header label at top.
 * 
 * @author chr_schwarz
 */
public class CubicTestFreeformLayer extends FreeformLayer {

	private CubicTestHeaderLabel header;
	
	public CubicTestFreeformLayer(CubicTestHeaderLabel header) {
		this.header = header;
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		// initializing vertical space, to prevent multiple columns:
		setSize(new Dimension(1, 9999));

		if (this.getChildren().contains(header))
			index ++;
		super.add(figure, constraint, index);
	}
	
}
