/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.cubictest.common.exception.CubicException;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Special header label for CubicTest abstract pages.
 * Returns a wide preferred size. 
 *
 * @author chr_schwarz
 */
public class CubicTestHeaderLabel extends CubicTestLabel {
	
	private CubicTestGroupFigure parent;
	private boolean fullWidth;

	public CubicTestHeaderLabel(String text, CubicTestGroupFigure parentAbstractPage, boolean fullWidth){
		super(text);
		this.parent = parentAbstractPage;
		this.fullWidth = fullWidth;
		if (parentAbstractPage == null) {
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
		if (fullWidth) {
			int parentWidth = parent.getSize().width;
			return new Dimension(parentWidth - 4, orig.height);
		}
		else {
			return orig;
		}
	}
}