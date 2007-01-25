/*
 * Created on 11.feb.2006
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
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

/**
 * Figure for group of elements.
 * Used for Contexts and Pages/Commons.
 * 
 * @author chr_schwarz
 */
public class CubicTestGroupFigure extends Figure {
	public static Color pageColor = new Color(null, 255, 255, 255);
	protected CubicTestHeaderLabel header;
	private String tooltipText;

	public CubicTestGroupFigure(String title, boolean fullHeaderWidth){
		header = new CubicTestHeaderLabel(title, this, fullHeaderWidth);
		header.setBorder(new HeaderBorder());
		if (fullHeaderWidth) {
			header.setTextPlacement(PositionConstants.NORTH);
		} else {
			header.setTextPlacement(PositionConstants.EAST);
		}
		
		setLayoutManager(ViewUtil.getFlowLayout());
		setBorder(new LineBorder(ColorConstants.black));
		
		setOpaque(true);
		add(header);
	}

	public void setSelected(boolean isSelected)	{
		LineBorder lineBorder = (LineBorder) getBorder();
		if (isSelected)
			lineBorder.setWidth(2);
		else
			lineBorder.setWidth(1);
		header.setSelected(isSelected);
	}
	
	/**
	 * @return returns the label used to edit the name
	 */
	public CubicTestLabel getHeader(){
		return header;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#add(org.eclipse.draw2d.IFigure, java.lang.Object, int)
	 */
	public void add(IFigure figure, Object constraint, int index) {
		IFigure parent = getParent();
		while (parent != null && !(parent instanceof CubicTestFreeformLayer)) {
			parent = parent.getParent();
		}
		if (parent instanceof CubicTestFreeformLayer) {
			//expand scrollable surface to prevent multiple columns:
			parent.setSize(new Dimension(1, 9999));
		}
		
		if (figure.equals(header)){
			super.add(figure,constraint,index);
		} else
			super.add(figure, constraint, index + 1 );
	}

	/**
	 * @param title
	 */
	public void setText(String text) {
		header.setText(text);		
	}
	
	public String getText(){
		return header.getText();
	}

	public void setTooltipText(String tooltipText) {
		this.tooltipText = tooltipText;
		header.setTooltipText(tooltipText);
	}
	
	@Override
	public IFigure getToolTip() {
		return new Label(StringUtils.replace(tooltipText, "$labelText", header.getFullText()));
	}
}
