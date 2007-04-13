/*
 * Created on 26.aug.2006
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model;


/**
 * The different types of element identification.
 *
 * @author skyttere
 */
public enum IdentifierType {

	//params: displayValue, description, isBoolean
	
	/**
	 * Returning the first hit when indentify in this order;
	 * the text in a label element;
	 * the value attribute for different buttons;
	 * and the text before or after the element.
	 */
	LABEL("Label", "Checks first for text in label element for most elements, \n" +
			"then the text before or after the element. \n" +
			"For buttons the value attribute is checked.", false),

	/** HTML ID attribute */
	ID("Id", "Check the ID attribute.", false),

	/** HTML name attribute */
	NAME("Name", "Check the name attribute.", false),

	/** HTML value attribute */
	VALUE("Value", "Check the value attribute for input elements.", false),

	/** HTML href attribute */
	HREF("Href", "Check the href; meaning where the link points to.", false),

	/** HTML src attribute */
	SRC("Src", "Check the source attribute.", false),
	
	/** HTML checked attribute */
	CHECKED("Checked", "Check whether the element is checked or not.", true),

	/** HTML selected attribute */
	SELECTED("Selected", "Check whether the element is selected or not.", true),

	/** HTML is mulitselectEnabled */
	MULTISELECT("Multiselect", "Check whether it is possible to select several elements.", true),
	
	/** HTML title attribute */
	TITLE("Tooltip", "Check the tooltip text (HTML title attribute).", false),
	
	/** Element index in page */
	INDEX("Index", "Check the element's index in the page / Context. " +
			"Default operator is \"=\" (equality), but \">\", \">=\", \"<\" and \"<=\" is also supported", false),
	
	/** XPath to the element */
	XPATH("XPath", "Check the XPath to the element", false),

	/** XPath to the element */
	ELEMENT_NAME("Element name", "Check the name of the HTML element (e.g. \"table\")", false);

	private String displayValue;
	private String description;
	private boolean isBoolean;

	private IdentifierType(String displayValue, String description, boolean isBoolean){
		this.displayValue = displayValue;
		this.description = description;
		this.isBoolean = isBoolean;
	}


	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return displayValue;
	}
	
	public boolean isBoolean() {
		return isBoolean;
	}

}
