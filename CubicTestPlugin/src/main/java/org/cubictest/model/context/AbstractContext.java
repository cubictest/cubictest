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
package org.cubictest.model.context;

import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.ELEMENT_NAME;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.INDEX;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.TestPartStatus;

/**
 * Base class for contexts that are "part of page"-contexts.
 * More specifically it represents a HTML element containing child elements/descendants.
 * 
 * @author chr_schwarz
 */
public abstract class AbstractContext extends PageElement implements IContext {
	
	private List<PageElement> elements = new ArrayList<PageElement>();

	
	public void addElement(PageElement pe) {
		//idempotent:
		if (!elements.contains(pe)) {
			elements.add(pe);
			firePropertyChange(PropertyAwareObject.CHILD,null,pe);
		}
	}

	public void addElement(PageElement pe, int index) {
		//idempotent:
		if (!elements.contains(pe)) {
			elements.add(index,pe);
			firePropertyChange(PropertyAwareObject.CHILD,null,pe);
		}
	}

	public void removeElement(PageElement pe) {
		elements.remove(pe);
		firePropertyChange(PropertyAwareObject.CHILD,pe,null);
	}
	
	/**
	 * @param child
	 * @param newIndex
	 */
	public void switchPageElement(PageElement child, int newIndex) {
		int i = elements.indexOf(child);
		if (i <= newIndex) newIndex--;
		if (newIndex < 0) newIndex = 0;
		elements.remove(child);
		elements.add(newIndex,child);
		firePropertyChange(PropertyAwareObject.LAYOUT,null,child);
	}

	public List<PageElement> getRootElements() {
		return elements;
	}
	
	public void setElements(List<PageElement> elements) {
		this.elements = elements;
	}
	
	/**
	 * @param element
	 * @return
	 */
	public int getElementIndex(PageElement element) {
		return elements.indexOf(element);
	}
	
	@Override
	public ActionType getDefaultAction() {
		return ActionType.CLICK;
	}

	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(IdentifierType.LABEL);
		list.add(ELEMENT_NAME);
		list.add(ID);
		list.add(IdentifierType.NAME);
		list.add(INDEX);
		list.add(CLASS);
		return list;
	}
	
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
		for (PageElement pe : getRootElements()) {
			pe.resetStatus();
		}
	}
	
	public boolean contains(PageElement pe) {
		if(elements.contains(pe))
			return true;
		for(PageElement child : elements)
			if(child instanceof IContext)
				if(((IContext)child).contains(pe))
					return true;
		return false;
	}
	
	@Override
	protected void setDefaultIdentifierValues() {
		//leave the ID's as the constructor made them
	}
	
	
	/**
	 * Gets all page elements of a page in flat structure (traverse contexts). 
	 */
	public List<PageElement> getFlattenedElements() {
		List<PageElement> flattenedElements = new ArrayList<PageElement>(); 

		for (PageElement element: getRootElements()){

			if(element instanceof IContext){
				flattenedElements.add(element);
				flattenedElements.addAll(((IContext) element).getFlattenedElements());
			}
			else {
				flattenedElements.add(element);
			}
		}
		return flattenedElements;
	}
}
