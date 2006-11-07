/*
 * Created on 09.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model.formElement;

import static org.cubictest.model.ActionType.*;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;

/**
 * Base class for text input.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public abstract class AbstractTextInput extends FormElement {

	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ENTER_TEXT);
		actions.add(ENTER_PARAMETER_TEXT);
		actions.add(KEY_PRESSED);
		actions.add(CLEAR_ALL_TEXT);
		actions.addAll(super.getActionTypes());
		return actions;
	}
	
	@Override
	public ActionType getDefaultAction() {
		return ActionType.ENTER_TEXT;
	}
}
