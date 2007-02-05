/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.interfaces;

import org.cubictest.model.FormElement;
import org.cubictest.model.PageElement;
import org.jdom.Element;

public interface IPageElementConverter {
	public class UnknownPageElementException extends Exception {}

	public Element convert(PageElement pe)
			throws UnknownPageElementException;

	public Element labelFormElement(FormElement element, Element element2);

}