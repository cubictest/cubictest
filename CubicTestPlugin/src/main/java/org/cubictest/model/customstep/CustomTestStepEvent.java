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
package org.cubictest.model.customstep;

public class CustomTestStepEvent {

	private final String key;
	private final Object oldValue;
	private final Object newValue;

	public CustomTestStepEvent(String key, Object oldValue, Object newValue) {
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String getKey() {
		return key;
	}
	
	public Object getOldValue() {
		return oldValue;
	}
	
	public Object getNewValue() {
		return newValue;
	}

}
