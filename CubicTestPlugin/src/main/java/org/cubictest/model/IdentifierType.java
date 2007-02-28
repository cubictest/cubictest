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
	 * the value attritute for different buttons;
	 * and the first input the after the text in that context.
	 */
	LABEL("Label"),

	/** HTML ID attribute */
	ID("ID"),

	/** HTML name attribute */
	NAME("Name"),

	/** HTML value attribute */
	VALUE("Value"),

	/** HTML href attribute */
	HREF("Href"),

	/** HTML src attribute */
	SRC("Src")
	;

	private String displayValue;

	private IdentifierType(String displayValue){
		this.displayValue = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}

}
