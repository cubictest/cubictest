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
	
	@Override
	protected Image getImage(boolean not) {
		String key = CubicTestImageRegistry.TITLE_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}

	@Override
	protected String getToolTipText() {
		String not = getModel().isNot()? "NOT " : "";
		return "Check " + not + "page title equals: $labelText";
	}

}
