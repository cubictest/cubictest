/*
 * Created on 29.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model;

import java.util.List;

public interface IActionElement {
	
	public List<ActionType> getActionTypes();
	
	public ActionType getDefaultAction();
	
	public String getDescription();

	public String getType();
	
}
