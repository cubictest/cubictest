/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;

import org.cubictest.model.Title;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author SK Skytteren
 * Contoller for the <code>Text</code> model.
 *
 */
public class PageTitleEditPart extends PageElementEditPart{

	/**
	 * Constructor for <code>PageTextEditPart</code>.
	 * @param text the model
	 */
	public PageTitleEditPart(Title title) {
		setModel(title);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label =  new TestStepLabel(((Title)getModel()).getDirectEditIdentifier().getValue());
		
		label.setLayoutManager(ViewUtil.getFlowLayout());
		label.setLabelAlignment(PositionConstants.LEFT);
		label.setIcon(getImage(((Title)getModel()).isNot()));
		label.setTooltipText("Check page title equals: $labelText");
		return label;
	}

	/**
	 * As the <code>getPropertyDescriptors()</code> method but uses the <code>i</code>
	 * for setting the catergory.
	 * @param i
	 * @return
	 */
	public IPropertyDescriptor[] getPropertyDescriptors(int i){
		String cat = getType();
		if (i != -1) cat = i + ": " +cat;
		
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		TextPropertyDescriptor tpd = new TextPropertyDescriptor(this,"Text");
		tpd.setCategory(cat);
		properties.add(tpd);
		return (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});
	}
	
	protected Image getImage(boolean not) {
		String key = CubicTestImageRegistry.TITLE_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}

}
