/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.formElement;

/**
 * A text field (html input element)
 * 
 * @author SK Skytteren
 *
 */
public class TextField extends AbstractTextInput {
	@Override
	public String getType() {
		return "TextField";
	}
}
