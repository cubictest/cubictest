/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.context.IContext;

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

		if (pe == null)
			return actionTypes;
		
		for(ActionType action : pe.getActionTypes()){
			if(ActionType.ENTER_PARAMETER_TEXT.equals(action) && test.getParamList() == null) {
				continue;
			}
			actionTypes.add(action);
		}
		return actionTypes;
	}
	
	
	/**
	 * Util method for getting all page elements of a page (traverse contexts). 
	 */
	public static List<PageElement> getFlattenedPageElements(List<PageElement> elements) {
		List<PageElement> flattenedElements = new ArrayList<PageElement>(); 

		for (PageElement element: elements){
			if(element.getActionTypes().size() == 0) {
				continue;
			}

			if(element instanceof IContext){
				flattenedElements.addAll(getFlattenedPageElements(((IContext) element).getRootElements()));
				flattenedElements.add(element);
			}
			else {
				flattenedElements.add(element);
			}
		}
		return flattenedElements;
	}
	
	public static String getLabel(IActionElement element, List<IActionElement> otherElements) {
		String res = "";
		if (element instanceof PageElement) {
			PageElement pageElement = (PageElement) element;
			res = getDefaultLabel(pageElement);
			
			String idInfo = "";
			String contextInfo = "";
			//check for similar elements, if found, add more info about identifiers
			for (IActionElement ae : otherElements) {
				if (ae.equals(element)) {
					continue;
				}
				if (ae instanceof IContext && ae instanceof PageElement) {
					if (((IContext) ae).contains(pageElement)) {
						//we have found the parent context
						contextInfo = ", context: '" + ((PageElement)ae).getDirectEditIdentifier().getValue() + "'";
					}
				}
				if (ae instanceof PageElement && getDefaultLabel((PageElement)ae).equals(getDefaultLabel(pageElement))) {
					idInfo = ", " + pageElement.identifierListToString();
				}
			}
			res = res + contextInfo + idInfo;
		}
		else {
			res = element.getType() + ": " + element.getDescription();						
		}
		
		return res;
	}


	private static String getDefaultLabel(PageElement pageElement) {
		return pageElement.getDirectEditIdentifier().getValue() + " (" + pageElement.getType() + ")";
	}
}
