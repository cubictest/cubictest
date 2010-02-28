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
package org.cubictest.model.customstep.data;

public class CustomTestStepDataEvent {
	
	private final EventType type;
	private final String oldText;
	private final String text;
	private CustomTestStepData data;

	public enum EventType{DISPLAY_TEXT, PATH};
	
	public CustomTestStepDataEvent(CustomTestStepData data, EventType type, String oldText, String text) {
		this.type = type;
		this.oldText = oldText;
		this.text = text;
		this.data = data;
	}

	public EventType getType() {
		return type;
	}
	
	public String getOldText() {
		return oldText;
	}
	
	public String getNewText() {
		return text;
	}
	
	public CustomTestStepData getData() {
		return data;
	}

}
