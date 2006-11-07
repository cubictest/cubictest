/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.context.IContext;


/**
 * @author Stein Kåre Skytteren
 *
 */
public abstract class AbstractPage extends TransitionNode implements Cloneable, IContext {

	private List<PageElement> elements = new ArrayList<PageElement>();
	private String name;
	
	public void addElement(PageElement pe, int index){
		//idempotent:
		if (!elements.contains(pe)) {
			elements.add(index, pe);
			firePropertyChange(CHILD,null,pe);
		}
	}
	
	public void addElement(PageElement pe){
		//idempotent:
		if (!elements.contains(pe)) {
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
		elements.add(newIndex,child);
		firePropertyChange(PropertyAwareObject.LAYOUT,null,child);
	}

	/**
	 * @param element
	 * @return
	 */
	public int getElementIndex(PageElement element) {
		return elements.indexOf(element);
	}

	public String getName() {
		if(name == null) {
			name = "";
		}
		return name;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(PropertyAwareObject.NAME, oldName, name);
	}
	
	@Override
	public void resetStatus() {
		for (PageElement e: elements)
			e.resetStatus();	
	}
	
	@Override
	public AbstractPage clone() throws CloneNotSupportedException {
		AbstractPage clone = (AbstractPage)super.clone();
		List<PageElement> elements = new ArrayList<PageElement>(); 
		for(PageElement element : this.elements){
			elements.add(element.clone());
		}
		clone.elements = elements;
		clone.setInTransition(null);
		clone.setOutTransitions(new ArrayList<Transition>());
		return clone;
	}
	
	public ArrayList<Link> getLinkElements(){
		ArrayList<Link> linkElements = new ArrayList<Link>();
		for (PageElement element : getElements()){
			if (element instanceof Link)
				linkElements.add((Link)element);
		}
		return linkElements;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + ": " + getName();
	}

}
