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

public class WebBrowser extends PropertyAwareObject implements IActionElement{

	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.GO_BACK);
		//actions.add(ActionType.GO_FORWARD);
		actions.add(ActionType.REFRESH);
		actions.add(ActionType.SWITCH_WINDOW_BY_NAME);
		actions.add(ActionType.NO_ACTION);
		return actions;
	}

	public ActionType getDefaultAction() {
		return ActionType.GO_BACK;
	}

	public String getDescription() {
		return "Window";
	}

	public String getType() {
		return "WebBrowser";
	}

	@Override
	public void resetStatus() {
	}

}