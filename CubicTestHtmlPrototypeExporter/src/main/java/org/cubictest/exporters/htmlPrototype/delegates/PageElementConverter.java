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
package org.cubictest.exporters.htmlPrototype.delegates;

import org.cubictest.common.utils.TextUtil;
import org.cubictest.exporters.htmlPrototype.interfaces.IPageElementConverter;
import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.formElement.Button;
import org.jdom.Element;

public class PageElementConverter implements IPageElementConverter {
	
	/* (non-Javadoc)
	 * @see org.cubictest.export.htmlSkeleton.interfaces.IPageElementConverter#convert(org.cubictest.model.PageElement)
	 */
	public Element convert(PageElement pe) throws UnknownPageElementException {
		Element result;
		
		if (pe instanceof Link) {
			result = fromLink((Link) pe);
		} else if (pe instanceof Image) {
			result = fromImage((Image) pe);
		} else if (pe instanceof Text) {
			result = fromText((Text) pe);
		} else if (pe instanceof FormElement) {
			result = fromFormElement((FormElement) pe);
		} else if (pe instanceof AbstractContext) {
			result = fromContext((AbstractContext) pe);
		} else {
			throw new UnknownPageElementException();
		}
		
		if(pe.isNot()) {
			result.setAttribute("class", (result.getAttributeValue("class") != null ? result.getAttributeValue("class") + " " : "") + "not");
		}
		
		return result;
	}

	private Element fromImage(Image image) {
		Element linkElement = new Element("a");
		linkElement.setAttribute("href", "#");
		if(isPositive(image.getIdentifier(IdentifierType.SRC))) {
			Element imageElement = new Element("img");
			imageElement.setAttribute("src", image.getIdentifier(IdentifierType.SRC).getValue());
			imageElement.setAttribute("alt", image.getIdentifier(IdentifierType.ALT).getValue());
			imageElement.setAttribute("title", image.getIdentifier(IdentifierType.TITLE).getValue());
			linkElement.addContent(imageElement);
		} else {
			linkElement.addContent("[IMG]" + image.toString());
		}
		
		return linkElement;
	}

	private Element fromContext(AbstractContext context) {
		Element result = new Element("fieldset");
		result.setAttribute("id", getPositiveValue(context.getIdentifier(IdentifierType.ID)));
		Element legend = new Element("legend");
		legend.addContent(getPositiveValue(context.getIdentifier(IdentifierType.LABEL)));
		result.addContent(legend);
		for(PageElement element : context.getRootElements()) {
			try {
				result.addContent(convert(element));
			} catch (UnknownPageElementException e) {}
		}
		return result;
		
	}
	
	private Element fromFormElement(FormElement fe) {
		String name = getPositiveValue(fe.getIdentifier(IdentifierType.NAME));
		String value = "";
		if (fe instanceof Button) {
			value = getPositiveValue(fe.getIdentifier(IdentifierType.LABEL));
		}
		else {
			value = getPositiveValue(fe.getIdentifier(IdentifierType.VALUE));
		}
		String id = getPositiveValue(fe.getIdentifier(IdentifierType.ID));
		String type = fe.getClass().getSimpleName().toLowerCase();
		Element input;
		
		if(type.equals("select")) {
			input = new Element("select");
			Element option = new Element("option");
			option.setAttribute("value", value);
			option.setText(value);
			input.addContent(option);
		} else {
			input = new Element("input");
			input.setAttribute("type", type);
			input.setAttribute("value", value);
		}
		input.setAttribute("name", name);
		input.setAttribute("id", id);											
		return input;
	}
	
	public Element labelFormElement(FormElement fe, Element input) {
		if (isPositive(fe.getIdentifier(IdentifierType.LABEL))) {
			Element labeledElement = new Element("div");
			String name = TextUtil.camel(fe.getIdentifier(IdentifierType.LABEL).getValue());
			String type = fe.getClass().getSimpleName().toLowerCase();
			
			if(!type.equals("submit") && !type.equals("button")) {
				Element label = new Element("label");
				label.setText(fe.getDescription());
				label.setAttribute("for", name);				
				labeledElement.addContent(label);
			}
			labeledElement.addContent(input);
			return labeledElement;
		}
		else {
			//do nothing
			return input;
		}
	}

	private Element fromText(Text text) {
		if (isPositive(text.getIdentifier(IdentifierType.LABEL))) {
			Element textElement = new Element("p");
			textElement.setText(text.getIdentifier(IdentifierType.LABEL).getValue());
			return textElement;
		}
		return null;
	}

	private Element fromLink(Link link) {
		if (isPositive(link.getIdentifier(IdentifierType.LABEL))) {
			Element linkElement = new Element("a");
			linkElement.addContent(link.getIdentifier(IdentifierType.LABEL).getValue());
			linkElement.setAttribute("href", "#");
			return linkElement;
		}
		return null;
	}

	
	
	private boolean isPositive(Identifier id) {
		return id != null && id.isNotBlank() && id.getProbability() >= 0;
	}
	
	
	private String getPositiveValue(Identifier id) {
		if (isPositive(id)) {
			return id.getValue();
		}
		return "";
	}
}
