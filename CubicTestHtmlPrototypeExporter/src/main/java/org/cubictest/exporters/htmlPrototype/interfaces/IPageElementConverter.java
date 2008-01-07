/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Erlend S. Halvorsen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Erlend S. Halvorsen - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.htmlPrototype.interfaces;

import org.cubictest.model.FormElement;
import org.cubictest.model.PageElement;
import org.jdom.Element;

public interface IPageElementConverter {
	public class UnknownPageElementException extends Exception {

	private static final long serialVersionUID = 1L;}

	public Element convert(PageElement pe)
			throws UnknownPageElementException;

	public Element labelFormElement(FormElement element, Element element2);

}