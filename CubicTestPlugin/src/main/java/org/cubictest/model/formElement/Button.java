/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.formElement;

import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.TITLE;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;


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
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = super.getIdentifierTypes();
		list.add(IdentifierType.SRC);
		return list;
	}

}
