/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 * Contoller for the <code>Text</code> model.
 *
 */
public class PageTextEditPart extends PageElementEditPart{

	/**
	 * Constructor for <code>PageTextEditPart</code>.
	 * @param text the model
	 */
	public PageTextEditPart(Text text) {
		super();
		setModel(text);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label =  new TestStepLabel(((Text)getModel()).getDescription());
		label.setIcon(getImage(((Text)getModel()).isNot()));
		
		label.setLayoutManager(ViewUtil.getFlowLayout());
		label.setLabelAlignment(PositionConstants.LEFT);
		String not = ((PageElement) getModel()).isNot() ? "NOT " : "";
		label.setTooltipText("Check text " + not + "present: $labelText");
		return label;
	}
	
	protected Image getImage(boolean not) {
		String key = not ? CubicTestImageRegistry.NOT_TEXT_IMAGE : 
										CubicTestImageRegistry.TEXT_IMAGE;
		return CubicTestImageRegistry.get(key);
	}
}
