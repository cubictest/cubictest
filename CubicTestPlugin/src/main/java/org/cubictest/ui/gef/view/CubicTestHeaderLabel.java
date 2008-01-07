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

import org.cubictest.common.exception.CubicException;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Special header label for CubicTest abstract pages.
 * Returns a preferred size equal to surrounding page. 
 *
 * @author chr_schwarz
 */
public class CubicTestHeaderLabel extends CubicTestLabel {
	
	private Figure parent;
	private boolean useParentWidth;

	public CubicTestHeaderLabel(String text, Figure parentFigure, boolean fullWidth){
		super(text);
		this.parent = parentFigure;
		this.useParentWidth = fullWidth;
		if (parentFigure == null) {
			throw new CubicException("Null parent not allowed.");
		}
	}
	
	@Override
	public void setText(String s) {
		super.setText(s);
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension orig = super.getPreferredSize(wHint, hHint);
		if (useParentWidth) {
			int parentWidth = parent.getSize().width;
			return new Dimension(parentWidth, orig.height);
		}
		else {
			return orig;
		}
	}
	
	public Dimension getUnmodifiedPreferredSize() {
		return super.getPreferredSize(-1, -1);
	}
}