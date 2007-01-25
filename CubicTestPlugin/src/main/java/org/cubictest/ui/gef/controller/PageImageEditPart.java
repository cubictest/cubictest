/*
 * Created on 12.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Image;
import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;

/**
 * Controller for Image model.
 * @author chr_schwarz
 */
public class PageImageEditPart extends PageElementEditPart {
	
	public PageImageEditPart(Image image) {
		setModel(image);
	}

	@Override
	protected IFigure createFigure() {
		TestStepLabel label =  new TestStepLabel(((Image)getModel()).getDescription());
		label.setIcon(getImage(((Image)getModel()).isNot()));
		
		label.setLayoutManager(ViewUtil.getFlowLayout());
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setTooltipText("Check image present: $labelText");
		return label;
	}
	
	private org.eclipse.swt.graphics.Image getImage(boolean not) {
		String key = not ? CubicTestImageRegistry.IMAGE_IMAGE : 
										CubicTestImageRegistry.IMAGE_IMAGE;
		return  CubicTestImageRegistry.get(key);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		TestStepLabel figure = (TestStepLabel)getFigure();
		PageElement element = (PageElement) getModel();
		figure.setText(element.getDescription());
		figure.setIcon(getImage(element.isNot()));
	}
}
