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
