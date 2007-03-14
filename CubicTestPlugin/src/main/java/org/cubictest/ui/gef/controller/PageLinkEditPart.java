/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 * Contoller for the <code>Link</code> model.
 *
 */
public class PageLinkEditPart extends PageElementEditPart{

	/**
	 * Constructor for <code>PageLinkEditPart</code>.
	 * @param link the model
	 */
	public PageLinkEditPart(Link link) {
		setModel(link);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure(){
		TestStepLabel label = new TestStepLabel(((Link)getModel()).getDescription());
		label.setIcon(getImage(((Link)getModel()).isNot()));
		
		label.setLayoutManager(ViewUtil.getFlowLayout());
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setOpaque(true);
		String not = ((PageElement) getModel()).isNot() ? "NOT " : "";
		label.setTooltipText("Check link " + not + "present: $labelText");
		return label;
	}	
	
	protected Image getImage(boolean not) {
		String key = CubicTestImageRegistry.LINK_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}
}
