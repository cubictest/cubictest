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
import org.cubictest.common.utils.ViewUtil;
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
	
	protected CubicTestLabel label;
	private String tooltipText;

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
		return new Label(StringUtils.replace(tooltipText, "$labelText", label.getFullText()));
	}
	
	public void setToolTipText(String toolTipText) {
		this.tooltipText = toolTipText;
		label.setTooltipText(toolTipText);
	}
}
