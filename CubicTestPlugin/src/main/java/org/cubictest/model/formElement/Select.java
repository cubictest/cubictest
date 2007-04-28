/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.formElement;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.IContext;

//TODO: Temporarily extending text input, later add option elements
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
		identifierTypes.add(IdentifierType.NAME);
		identifierTypes.add(IdentifierType.ID);
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
	public List<PageElement> getElements() {
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
		for (PageElement pe : getElements()) {
			pe.resetStatus();
		}
	}

	public boolean contains(PageElement pe) {
		return elements.contains(pe);
	}
	
}
