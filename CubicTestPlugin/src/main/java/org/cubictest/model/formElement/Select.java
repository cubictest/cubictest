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
package org.cubictest.model.formElement;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.IContext;

/**
 * A select list ("drop down") that can contain Options.
 * 
 * @author Christian Schwarz
 *
 */
public class Select extends FormElement implements IContext {

	private List<PageElement> elements = new ArrayList<PageElement>();
	@Override
	public String getType() {
		return "Select";
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {
		List<IdentifierType> identifierTypes = new ArrayList<IdentifierType>();
		identifierTypes.add(IdentifierType.LABEL);
		identifierTypes.add(IdentifierType.ID);
		identifierTypes.add(IdentifierType.NAME);
		identifierTypes.add(IdentifierType.MULTISELECT);
		identifierTypes.add(IdentifierType.TITLE);
		return identifierTypes;
	}
	
	public void addElement(PageElement pe, int index){
		//idempotent:
		if (pe instanceof Option && !elements.contains(pe)) {
			((Option) pe).setParent(this);
			elements.add(index, pe);
			firePropertyChange(CHILD,null,pe);
		}
	}
	
	public void addElement(PageElement pe){
		//idempotent:
		if (pe instanceof Option && !elements.contains(pe)) {
			((Option) pe).setParent(this);
			elements.add(pe);
			firePropertyChange(CHILD,null,pe);
		}
	}
	/**
	 * @return Returns the elements.
	 */
	public List<PageElement> getRootElements() {
		return elements;
	}
	/**
	 * @param elements The elements to set.
	 */
	public void setElements(List<PageElement> elements) {
		for (PageElement element : elements) {
			((Option) element).setParent(this);
		}
		this.elements = elements;
	}
	
	public void removeElement(PageElement pe){
		elements.remove(pe);
		firePropertyChange(CHILD,pe,null);
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
		addElement(child, newIndex);
		firePropertyChange(PropertyAwareObject.LAYOUT,null,child);
	}

	/**
	 * @param element
	 * @return
	 */
	public int getElementIndex(PageElement element) {
		return elements.indexOf(element);
	}

	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
		for (PageElement pe : getRootElements()) {
			pe.resetStatus();
		}
	}

	public boolean contains(PageElement pe) {
		return elements.contains(pe);
	}
	
	/**
	 * Gets all page elements of a page in flat structure (traverse contexts). 
	 */
	public List<PageElement> getFlattenedElements() {
		return getRootElements();
	}
	
}
