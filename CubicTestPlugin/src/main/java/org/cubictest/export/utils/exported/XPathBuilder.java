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
package org.cubictest.export.utils.exported;

import static org.cubictest.model.IdentifierType.CHECKED;
import static org.cubictest.model.IdentifierType.ELEMENT_NAME;
import static org.cubictest.model.IdentifierType.FRAME_TYPE;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.MULTISELECT;
import static org.cubictest.model.IdentifierType.SELECTED;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Moderator;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.Frame;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;


/**
 * Class for building XPaths based on page elements and its surrounding contexts.
 * 
 * @author Christian Schwarz
 *
 */
public class XPathBuilder {

	
	public static final String TEXTFIELD_ATTRIBUTES = "[@type='text' or not(@type)]";

	public static String getXPathForSingleElement(IActionElement element) {
		return getXPathForSingleElement(element, false);
	}

	/**
	 * Get the string that represents the Selenium locator-string for the element.
	 * @param element
	 * @param useNamespace 
	 * @return
	 */
	public static String getXPathForSingleElement(IActionElement element, boolean useNamespace) {
		PageElement pe = (PageElement) element;
		PredicateSeperator predicateSeperator = new PredicateSeperator();

		String predicates = 
				getIndexAssertion(pe, predicateSeperator) + 
				getLabelAssertion(pe, predicateSeperator, useNamespace) + 
				getAttributeAssertions(pe, predicateSeperator); 

		if (StringUtils.isBlank(predicates)) {
			return getElementType(pe);
		}
		return (useNamespace ? "x:" : "") + getElementType(pe) + "[" + predicates + "]";
	}
	
	
	
	
	/**
	 * Get string to assert the index of the element (if ID present).
	 */
	private static String getIndexAssertion(PageElement pe, PredicateSeperator predicateSeperator) {
		String result = predicateSeperator.getStartString();
		
		//Start with index attribute (if it exists) to make XPath correct:
		Identifier id = pe.getIdentifier(INDEX);
		if (id != null && id.isNotIndifferent()) {
			String value = pe.getIdentifier(INDEX).getValue();
			String operator = "=";
			if (value.startsWith("<=") || value.startsWith(">=")) {
				operator = value.substring(0, 2);
				value = value.substring(2);
			}
			else if (value.startsWith("<") || value.startsWith(">")) {
				operator = value.substring(0, 1);
				value = value.substring(1);
			}
			try {
				int index = Integer.parseInt(value);
				result += "position()" + operator + index;
			}
			catch (NumberFormatException e) {
				throw new CubicException("\"Index\" identifier of page element [" + pe.toString() + "] is not a number!");
			}
		}
		
		if (result.equals(predicateSeperator.getStartString())) {
			return "";
		}
		else {
			predicateSeperator.setNeedsSeparator(true);
			return result;
		}
	}
	
	
	private static String getLabelAssertion(PageElement pe, PredicateSeperator predicateSeperator, boolean useNamespace) {
		String result = predicateSeperator.getStartString();
		
		Identifier id = pe.getIdentifier(LABEL);
		if (id != null && id.isNotIndifferent()) {
			if (pe instanceof Text) {
				result += "contains(normalize-space(.), '" + id.getValue() + "')";
			}
			else if (pe instanceof Link || pe instanceof Option) {
				result += getPageValueCheck(id, "normalize-space(text())");
			}
			else if (pe instanceof Button) {
				result += getIdentifierCondition(id);
			}
			else {
				//get first element that has "id" attribute equal to the "for" attribute of label with the specified text:
				String labelCondition = getPageValueCheck(id, "normalize-space(text())");
				result += "@id" + getStringComparisonOperator(id) + 
					"(//" + (useNamespace ? "x:" : "") + "label[" + labelCondition + "]/@for)";
			}
		}

		if (result.equals(predicateSeperator.getStartString())) {
			return "";
		}
		else {
			predicateSeperator.setNeedsSeparator(true);
			return result;
		}
	}

	/**
	 * Get string to assert for all the page elements Identifier/HTML attribute values.
	 * E.g. [@id="someId"]
	 */
	private static String getAttributeAssertions(PageElement pe, PredicateSeperator predicateSeperator) {
		String result = predicateSeperator.getStartString();
		int i = 0;
		
		for (Identifier id : pe.getNonIndifferentIdentifierts()) {
			IdentifierType type = id.getType();
			if (type.equals(LABEL) || type.equals(INDEX) || 
					type.equals(ELEMENT_NAME) || type.equals(FRAME_TYPE)) {
				//label are not HTML attributes, index and element name are handled elsewhere
				continue;
			}
			if (i > 0) {
				result += " and ";
			}
			
			result += getIdentifierCondition(id);
			i++;
		}		
		
		if (result.equals(predicateSeperator.getStartString())) {
			return "";
		}
		else {
			predicateSeperator.setNeedsSeparator(true);
			return result;
		}
	}


	private static String getIdentifierCondition(Identifier id) {
		String result = "";
		if (id.getType().equals(CHECKED) || id.getType().equals(SELECTED) || id.getType().equals(MULTISELECT)) {
			//idType with no value
			if (id.getProbability() > 0) {
				result += "@" + ExportUtils.getHtmlIdType(id)+ "=''";
			}
			else {
				result += "not(@" + ExportUtils.getHtmlIdType(id) + ")";
			}
		}
		else {
			//normal ID type (name, value)
			String attr = getAttributeToCheck(id);
			result += getPageValueCheck(id, attr);
		}
		return result;
	}



	/**
	 * Check a page value against an identifier
	 * @param id the identifier
	 * @param pageValue the XPath fragement for which page value to check
	 * @return
	 */
	private static String getPageValueCheck(Identifier id, String pageValue) {
		String result = "";
		String comparisonOperator = getStringComparisonOperator(id);
		if (id.getModerator().equals(Moderator.EQUAL)) {
			//normal equal check
			result += pageValue + comparisonOperator + "'" + id.getValue() + "'";
		}
		else {
			String prefixOperator = getPrefixComparisonOperator(id);
			if (id.getModerator().equals(Moderator.BEGIN)) {
				result += "substring(" + pageValue + ", 0, string-length('" + id.getValue() + "') + 1) " + comparisonOperator + " '" + id.getValue() + "'";
			}
			else if (id.getModerator().equals(Moderator.CONTAIN)) {
				result += prefixOperator + "(contains(" + pageValue + ", " + "'" + id.getValue() + "'))";
			}
			else if (id.getModerator().equals(Moderator.END)) {
				result += "substring(" + pageValue + ", string-length(" + pageValue + ") - string-length(" + "'" + id.getValue() +"')" +
						" + 1, string-length('" + id.getValue() + "')) " + comparisonOperator + " '" + id.getValue() + "'";
			}
		}
		return result;
	}




	private static String getAttributeToCheck(Identifier id) {
		return "@" + ExportUtils.getHtmlIdType(id);
	}


	private static String getPrefixComparisonOperator(Identifier id) {
		if (id.getProbability() < 0) {
			return "not";
		}
		return "";
	}



	private static String getStringComparisonOperator(Identifier id) {
		if (id.getProbability() < 0) {
			return "!=";
		}
		return "=";
	}
	
		
	/**
	 * Get the HTML element type for the page element.
	 * @param pe
	 * @return
	 */
	private static String getElementType(PageElement pe) {
		if (pe instanceof Select)
			return "select";
		if (pe instanceof Option)
			return "option";
		if (pe instanceof Button)
			return "input[@type='button' or @type='submit' or @type='image' or @type='reset']";
		if (pe instanceof TextField)
			return "input" + TEXTFIELD_ATTRIBUTES;
		if (pe instanceof Password)
			return "input[@type='password']";
		if (pe instanceof Checkbox)
			return "input[@type='checkbox']";
		if (pe instanceof RadioButton)
			return "input[@type='radio']";
		if (pe instanceof Link)
			return "a";
		if (pe instanceof Image)
			return "img";
		if (pe instanceof Text)
			return "*";
		if (pe instanceof TextArea)
			return "textarea";
		if (pe instanceof Frame){
			if(pe.getIdentifier(IdentifierType.FRAME_TYPE).getProbability() >0){
				if ("iframe".equals(pe.getIdentifier(IdentifierType.FRAME_TYPE).getValue())) {
					return "iframe";
				} else {
					return "frame";
				}
			}
			else if(pe.getIdentifier(IdentifierType.FRAME_TYPE).getProbability() < 0){
				if ("iframe".equals(pe.getIdentifier(IdentifierType.FRAME_TYPE).getValue())) {
					return "frame";
				} else {
					return "iframe";
				}
			} 
			else {
				return "*";
			}
		}
		if (pe instanceof AbstractContext) {
			Identifier elementName = pe.getIdentifier(ELEMENT_NAME);
			if (elementName != null && StringUtils.isNotBlank(elementName.getValue())) {
				return elementName.getValue();
			}
			else {
				return "*";
			}
		}

		throw new ExporterException("Unknown element type: " + pe);
	}


	
	static class PredicateSeperator {
		private boolean needsSeparator = false;

		public void setNeedsSeparator(boolean needsSeparator) {
			this.needsSeparator = needsSeparator;
		}
		
		public String getStartString() {
			if (needsSeparator) {
				return " and ";
			}
			return "";
		}
	}
	

}
