package org.cubictest.export.utils;

import static org.cubictest.model.IdentifierType.CHECKED;
import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.ELEMENT_NAME;
import static org.cubictest.model.IdentifierType.HREF;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.MULTISELECT;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.SELECTED;
import static org.cubictest.model.IdentifierType.SRC;
import static org.cubictest.model.IdentifierType.TITLE;
import static org.cubictest.model.IdentifierType.VALUE;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.context.AbstractContext;
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

	
	public static final String FIREEVENT = "fireEvent";
	
	/**
	 * Get the string that represents the Selenium locator-string for the element.
	 * @param element
	 * @return
	 */
	public static String getXPath(IActionElement element) {
		if (element instanceof WebBrowser) {
			return "";
		}
		PageElement pe = (PageElement) element;
		PredicateSeperator predicateSeperator = new PredicateSeperator();

		String predicates = 
				getIndexAssertion(pe, predicateSeperator) + 
				getLabelAssertion(pe, predicateSeperator) + 
				getAttributeAssertions(pe, predicateSeperator); 

		if (StringUtils.isBlank(predicates)) {
			return getElementType(pe);
		}
		return getElementType(pe) + "[" + predicates + "]";
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
			int index = Integer.parseInt(value);
			result += "position()" + operator + index;
		}
		
		if (result.equals(predicateSeperator.getStartString())) {
			return "";
		}
		else {
			predicateSeperator.setNeedsSeparator(true);
			return result;
		}
	}
	
	
	private static String getLabelAssertion(PageElement pe, PredicateSeperator predicateSeperator) {
		String result = predicateSeperator.getStartString();
		
		Identifier id = pe.getIdentifier(LABEL);
		if (id != null && id.isNotIndifferent()) {
			String labelText = id.getValue();
			
			String comparisonOperator = "=";
			if (pe.getIdentifier(LABEL).getProbability() < 0) {
				comparisonOperator = "!=";
			}

			if (pe instanceof Text) {
				result += "contains(normalize-space(.), \"" + labelText + "\")";
			}
			else if (pe instanceof Link || pe instanceof Option) {
				result += "normalize-space(text())" + comparisonOperator + "\"" + labelText + "\"";
			}
			else if (pe instanceof Button) {
				result += "@value" + comparisonOperator + "\"" + labelText + "\"" ;
			}
			else {
				//get first element that has "id" attribute equal to the "for" attribute of label with the specified text:
				result += "@id" + comparisonOperator + "(//label[text()=\"" + labelText + "\"]/@for)";
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
	 * Get the Selenium ID type based on the specified Identifier.
	 * Also works for HTML ID's except for LABEL.
	 * @param id
	 * @return
	 */
	public static String getIdType(Identifier id) {
		if (id.getType().equals(LABEL)) {
			return "label";
		}
		else if (id.getType().equals(NAME)) {
			return "name";
		}
		else if (id.getType().equals(ID)) {
			return "id";
		}
		else if (id.getType().equals(VALUE)) {
			return "value";
		}
		else if (id.getType().equals(HREF)) {
			return "href";
		}
		else if (id.getType().equals(SRC)) {
			return "src";
		}
		else if (id.getType().equals(TITLE)) {
			return "title";
		}
		else if (id.getType().equals(CHECKED)) {
			return "checked";
		}
		else if (id.getType().equals(SELECTED)) {
			return "selected";
		}
		else if (id.getType().equals(MULTISELECT)) {
			return "multiple";
		}
		else if (id.getType().equals(INDEX)) {
			return "index";
		}
		else if (id.getType().equals(CLASS)) {
			return "class";
		}
		return null;
	}
	
	

	/**
	 * Get string to assert for all the page elements Identifier/HTML attribute values.
	 * E.g. [@id="someId"]
	 */
	private static String getAttributeAssertions(PageElement pe, PredicateSeperator predicateSeperator) {
		String result = predicateSeperator.getStartString();
		int i = 0;
		
		for (Identifier id : pe.getNonIndifferentIdentifierts()) {
			if (id.getType().equals(LABEL) || id.getType().equals(INDEX) || id.getType().equals(ELEMENT_NAME)) {
				//label are not HTML attributes, index and element name are handled elsewhere
				continue;
			}
			if (i > 0) {
				result += " and ";
			}
			String comparisonOperator = "=";
			if (id.getProbability() < 0) {
				comparisonOperator = "!=";
			}
			
			if (id.getType().equals(CHECKED) || id.getType().equals(SELECTED) || id.getType().equals(MULTISELECT)) {
				//idType with no value
				if (id.getProbability() > 0) {
					result += "@" + getIdType(id)+ "=\"\"";
				}
				else {
					result += "not(@" + getIdType(id) + ")";
				}
			}
			else {
				//normal ID type (name, value)
				result += "@" + getIdType(id) + comparisonOperator + "\"" + id.getValue() + "\"";
			}
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
	
		
	/**
	 * Get the HTML element type for the page element.
	 * @param pe
	 * @return
	 */
	public static String getElementType(PageElement pe) {
		if (pe instanceof Select)
			return "select";
		if (pe instanceof Option)
			return "option";
		if (pe instanceof Button)
			return "input[@type=\"button\" or @type=\"submit\" or @type=\"image\"]";
		if (pe instanceof TextField)
			return "input[@type=\"text\"]";
		if (pe instanceof Password)
			return "input[@type=\"password\"]";
		if (pe instanceof Checkbox)
			return "input[@type=\"checkbox\"]";
		if (pe instanceof RadioButton)
			return "input[@type=\"radio\"]";
		if (pe instanceof Link)
			return "a";
		if (pe instanceof Image)
			return "img";
		if (pe instanceof Text)
			return "*";
		if (pe instanceof TextArea)
			return "textarea";
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
