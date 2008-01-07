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

import org.apache.commons.lang.StringUtils;
import org.cubictest.model.TestPartStatus;
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
	private boolean fullHeaderWidth;

	public CubicTestGroupFigure(String title, boolean fullHeaderWidth){
		header = new CubicTestHeaderLabel(title, this, fullHeaderWidth);
		header.setBorder(new HeaderBorder());
		if (fullHeaderWidth) {
			header.setTextPlacement(PositionConstants.NORTH);
		} else {
			header.setTextPlacement(PositionConstants.EAST);
		}
		this.fullHeaderWidth = fullHeaderWidth;
		
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
	@Override
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

	public void setStatus(TestPartStatus status) {
		getHeader().setStatus(status);
	}
	
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension dim = super.getPreferredSize(wHint, hHint).getCopy();
		if (!fullHeaderWidth) {
			dim.width = dim.width + 3;
		}
		return dim;
	}
}
