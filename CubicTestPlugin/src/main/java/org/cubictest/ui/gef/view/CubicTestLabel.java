/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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
		switch (status){
			case UNKNOWN:
				setOpaque(false);
			break;		
			case PASS:
				setBackgroundColor(ColorConstants.green);
				break;
			case FAIL:
				setBackgroundColor(ColorConstants.red);
				break;
			case EXCEPTION:
				setBackgroundColor(ColorConstants.orange);;
				break;
			default:
				setOpaque(false);
		}
	}
	
}