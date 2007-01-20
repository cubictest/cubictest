/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
