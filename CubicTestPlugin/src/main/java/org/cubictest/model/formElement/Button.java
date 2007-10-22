/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.formElement;

import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;


/**
 * A button on the page. Can be any type of button.
 * 
 * @author Christian Schwarz
 *
 */
public class Button extends FormElement {

	@Override
	public String getType() {
		return "Button";
	}

	@Override
	protected void setDefaultIdentifierValues() {
		getIdentifiers().get(0).setProbability(Identifier.MAX_PROBABILITY);
	}
}
