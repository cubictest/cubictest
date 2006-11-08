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
 * @author skyttere
 *
 */
public class Text extends PageElement {
	public String getType(){
		return "Text";
	}
	
	public List<ActionType> getActionTypes() {
		//empty list. Must use contexts for action types on texts
		return new ArrayList<ActionType>();
	}
}
