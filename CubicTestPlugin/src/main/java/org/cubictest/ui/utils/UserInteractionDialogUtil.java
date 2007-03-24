/*
 * Created on 23.mar.2007
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Test;

/**
 * Utils for user interactions dialog.
 * 
 * @author chr_schwarz
 */
public class UserInteractionDialogUtil {

	public static String[] getActionTypesForElement(IActionElement pe, Test test) {
		List<String> actionTypes = new ArrayList<String>();
		for(ActionType action : pe.getActionTypes()){
			if(ActionType.ENTER_PARAMETER_TEXT.equals(action) && test.getParamList() == null) {
				continue;
			}
			actionTypes.add(action.getText());
		}
		String[] actions = actionTypes.toArray(new String[0]);
		return actions;
	}
}
