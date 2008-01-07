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
package org.cubictest.model.context;

import java.util.List;

import org.cubictest.model.IDescription;
import org.cubictest.model.PageElement;

/**
 * Interface for elements that holds/maintains child elements.
 * Can be the page itself or a part of it (a HTML element containing child elements/descendants).
 * 
 * @author SK SKytteren
 */
public interface IContext extends IDescription {
	
	public void addElement(PageElement pe);
	public void addElement(PageElement pe, int index);
	public void removeElement(PageElement pe);
	public void switchPageElement(PageElement child, int newIndex);
	public int getElementIndex(PageElement element);
	public boolean contains(PageElement pe);
	
	/**
	 * Get all page elements in this context.
	 * @return
	 */
	public List<PageElement> getRootElements();
	public List<PageElement> getFlattenedElements();

}
