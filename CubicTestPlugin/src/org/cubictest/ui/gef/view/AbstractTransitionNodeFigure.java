/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author SK Skytteren
 *
 */
public class AbstractTransitionNodeFigure extends Figure {
	
	private CubicTestLabel label;

	public AbstractTransitionNodeFigure(){
		setLayoutManager(ViewUtil.getFlowLayout());

		setBorder(new LineBorder(ColorConstants.black));
		setMinimumSize(new Dimension(40,25));
		setBackgroundColor(ColorConstants.button);

		label = new CubicTestLabel("");
		add(label);
		setOpaque(true);
	}
	
	public void setText(String s) {
		label.setText(s);
	}

	public void setLabelLength(int length) {
		label.setLabelLength(length);
	}
}
