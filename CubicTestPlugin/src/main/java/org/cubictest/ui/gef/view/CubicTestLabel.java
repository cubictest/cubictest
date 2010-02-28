/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

/**
 * Special label for CubicTest that does not display more than 
 * x characters text (configurable).
 *
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class CubicTestLabel extends Label {
	
	private int length = 50;
	private String fullText;
	private String tooltipText;

	public CubicTestLabel(String text){
		super();
		setText(text);
		setOpaque(true);
		fullText = text;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Label#setText(java.lang.String)
	 */
	@Override
	public void setText(String s) {
		fullText = s;
		if (s != null && s.length() > length)
			s = s.substring(0,length).concat("...");
		super.setText(s);
	}


	public void setSelected(boolean selected) {
		if (selected) setForegroundColor(ColorConstants.darkGray);
		else setForegroundColor(ColorConstants.black);
	}

	public void setLabelLength(int length) {
		this.length = length;
	}

	public String getFullText() {
		return fullText;
	}
	
	@Override
	public IFigure getToolTip() {
		return new Label(StringUtils.replace(tooltipText, "$labelText", getFullText()));
	}
	
	public void setTooltipText(String s) {
		this.tooltipText = s;
	}
	
	public void setStatus(TestPartStatus status){
		setOpaque(true);
		if(status == null){
			setOpaque(false);
			return;
		}
		switch (status){
			case UNKNOWN:
				setOpaque(false);
			break;		
			case PASS:
				setBackgroundColor(ColorConstants.green);
				break;
			case WARN:
				setBackgroundColor(ColorConstants.yellow);
				break;
			case FAIL:
				setBackgroundColor(ColorConstants.red);
				break;
			case EXCEPTION:
				setBackgroundColor(ColorConstants.orange);
				break;
			default:
				setOpaque(false);
		}
	}
	
}