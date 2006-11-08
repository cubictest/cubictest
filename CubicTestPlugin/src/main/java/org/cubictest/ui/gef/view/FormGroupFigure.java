/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;

public class FormGroupFigure extends Figure{
	
	private FormGroupLabel nameLabel;

	public FormGroupFigure(String text){
		nameLabel = new FormGroupLabel(text);

		setLayoutManager(ViewUtil.getFlowLayout());
		setBorder(new LineBorder(ColorConstants.gray));
		setBackgroundColor(ColorConstants.menuBackground);
		setOpaque(true);
		add(nameLabel);
	}
	/**
	 * @return
	 */
	public FormGroupLabel getNameLabel() {
		return nameLabel;
	}
	
	public void setSelected(boolean isSelected){
		LineBorder lineBorder = (LineBorder) getBorder();
		if (isSelected)
			lineBorder.setWidth(2);
		else
			lineBorder.setWidth(1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#add(org.eclipse.draw2d.IFigure, java.lang.Object, int)
	 */
	public void add(IFigure figure, Object constraint, int index) {
		if(figure instanceof FormGroupLabel){
			super.add(figure,constraint,index);
		}else
			super.add(figure, constraint, index + 1);
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		nameLabel.setText(name);
	}
}
