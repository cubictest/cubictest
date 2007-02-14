/*
 * Created on 08.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model.context;

import java.util.List;

import org.cubictest.model.PageElement;

/**
 * Interface for elements that holds/maintains child elements.
 * Can be the page itself or a part of it (a HTML element containing child elements/descendants).
 * 
 * @author SK SKytteren
 */
public interface IContext {
	
	public void addElement(PageElement pe);
	public void addElement(PageElement pe, int index);
	public void removeElement(PageElement pe);
	public void switchPageElement(PageElement child, int newIndex);
	public int getElementIndex(PageElement element);
	public List<PageElement> getElements();
}
