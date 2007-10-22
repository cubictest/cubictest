/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Title of web application to assert present.
 * 
 * @author Christian Schwarz
 */
public class Title extends PageElement {
	
	@Override
	public String getType(){
		return "Title";
	}

	@Override
	public List<ActionType> getActionTypes() {
		return new ArrayList<ActionType>();
	}
	
	@Override
	public ActionType getDefaultAction() {
		return null;
	}
}
