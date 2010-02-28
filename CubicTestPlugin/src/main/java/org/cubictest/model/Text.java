/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
