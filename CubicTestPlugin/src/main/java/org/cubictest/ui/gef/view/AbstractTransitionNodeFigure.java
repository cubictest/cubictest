/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.apache.commons.lang.StringUtils;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;

/**
 * @author SK Skytteren
 *
 */
public class AbstractTransitionNodeFigure extends Figure {
	
	private CubicTestLabel label;
	private String toolTipText;

	public AbstractTransitionNodeFigure(){
		setLayoutManager(ViewUtil.getFlowLayout());

		setBorder(new LineBorder(ColorConstants.black));
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
	
	@Override
	public IFigure getToolTip() {
		return StringUtils.isBlank(toolTipText) ? null : new Label(toolTipText);
	}
	
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
		label.setTooltipText(toolTipText);
	}
}
