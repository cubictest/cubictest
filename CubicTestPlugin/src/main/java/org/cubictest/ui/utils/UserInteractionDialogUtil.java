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

	public static String[] getActionTypeLabelsForElement(IActionElement pe, Test test) {
		List<ActionType> types = getActionTypesForElement(pe, test);
		String[] actionTypes = new String[types.size()];
		int i = 0;
		for (ActionType type : types) {
			actionTypes[i] = type.getText();
			i++;
		}
		return actionTypes;
	}
	
	
	public static List<ActionType> getActionTypesForElement(IActionElement pe, Test test) {
		List<ActionType> actionTypes = new ArrayList<ActionType>();
		for(ActionType action : pe.getActionTypes()){
			if(ActionType.ENTER_PARAMETER_TEXT.equals(action) && test.getParamList() == null) {
				continue;
			}
			actionTypes.add(action);
		}
		return actionTypes;
	}
}
