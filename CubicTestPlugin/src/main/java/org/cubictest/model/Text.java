/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Text to assert present on page. Can be a substring of a larger string.
 * 
 * @author SK Skytteren
 *
 */
public class Text extends PageElement {
	
	@Override
	public String getType(){
		return "Text";
	}
	
	@Override
	public List<ActionType> getActionTypes() {
		//empty list. Must use contexts for action types on texts
		return new ArrayList<ActionType>();
	}
}
