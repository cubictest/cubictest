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

	/**
	 * Returning the first hit when indentify in this order;
	 * the text in a label element;
	 * the value attribute for different buttons;
	 * and the text before or after the element.
	 */
	LABEL("Label", "Checks first for text in label element for most elements, \n" +
			" then the text before or after the element \n. " +
			" For buttons the value attribute is checked."),

	/** HTML ID attribute */
	ID("Id", "Check the ID attribute."),

	/** HTML name attribute */
	NAME("Name", "Check the name attribute."),

	/** HTML value attribute */
	VALUE("Value", "Check the value attribute for input elements."),

	/** HTML href attribute */
	HREF("Href", "Checking the href; meaning where the link points to."),

	/** HTML src attribute */
	SRC("Src", "Checking if the source ends this attribute."),
	
	/** HTML checked attribute */
	CHECKED("Checked", "Check it the element is checked or not."),
	
	/** HTML is mulitselectEnabled */
	MULTISELECT("Multiselect", "Check if it is possible to select several elements."),
	
	/** HTML title attribute */
	TITLE("Title", "Check the title attribute; meaning the tooltip text."),
	
	//Added by Genesis Campos
	/** element index in page */
	INDEX("Index", "Checking the elements index in the page"),
	//End;
	
	/** XPath to the element */
	XPATH("XPath", "Checking the XPath to the element");

	private String displayValue;
	private String description;

	private IdentifierType(String displayValue, String description){
		this.displayValue = displayValue;
		this.description = description;
	}


	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return displayValue;
	}

}
