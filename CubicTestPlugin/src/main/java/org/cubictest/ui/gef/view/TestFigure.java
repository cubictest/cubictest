/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;

/**
 * @author SK Skytteren
 * 
 */
public class TestFigure extends ScalableFreeformLayeredPane {

	public TestFigure() {
		setOpaque(true);
		setBackgroundColor(ColorConstants.white);
		FreeformLayout layout = new FreeformLayout();
		setLayoutManager(layout);
	}
}
