/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.formElement;

import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import static org.cubictest.model.IdentifierType.*;



public class Button extends FormElement {

	
	public String getType() {
		return "Button";
	}
	
	@Override
	public IdentifierType getIdentifierType() {
		if (identifierType == null) {
			//defaulting to label:
			return LABEL;
		}
		return identifierType;
	}
}
