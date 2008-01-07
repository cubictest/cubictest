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
package org.cubictest.model.popup;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;

public abstract class JavaScriptPopup extends AbstractPage {

	@Override
	public void addElement(PageElement pe, int index){}
	
	@Override
	public void addElement(PageElement pe){}
	
	@Override
	public void setElements(List<PageElement> elements) {
	}
	
	@Override
	public void removeElement(PageElement pe){}

	@Override
	public void switchPageElement(PageElement child, int newIndex) {}

	@Override
	public int getElementIndex(PageElement element) {
		return getRootElements().indexOf(element);
	}
	
	@Override
	public void resetStatus() {
		for (PageElement e: getRootElements())
			e.resetStatus();	
	}
	
	@Override
	public ArrayList<Link> getLinkElements(){
		return new ArrayList<Link>();
	}
	
	@Override
	public String toString() {
		return getClass().getName() + ": " + getName();
	}

	public abstract String getType();
}
