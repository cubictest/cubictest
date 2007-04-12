/*
 * Created on 08.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model.context;

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

	public List<PageElement> getElements() {
		return elements;
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
		list.add(ID);
		list.add(INDEX);
		list.add(ELEMENT_NAME);
		return list;
	}
	
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
		for (PageElement pe : getElements()) {
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
}
