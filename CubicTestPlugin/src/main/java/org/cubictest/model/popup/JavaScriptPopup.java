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
		return getElements().indexOf(element);
	}
	
	@Override
	public void resetStatus() {
		for (PageElement e: getElements())
			e.resetStatus();	
	}
	
	@Override
	public AbstractPage clone() throws CloneNotSupportedException {
		AbstractPage clone = (AbstractPage) super.clone();
		List<PageElement> clonedElements = new ArrayList<PageElement>(); 
		for(PageElement element : getElements()){
			clonedElements.add(element.clone());
		}
		clone.setElements(clonedElements);
		return clone;
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
