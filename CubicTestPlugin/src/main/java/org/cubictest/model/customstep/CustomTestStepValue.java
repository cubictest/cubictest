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
package org.cubictest.model.customstep;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CustomTestStepValue {
	
	private CustomTestStepParameter parameter;
	private String value = "";
	
	private transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);

	public CustomTestStepValue(CustomTestStepParameter parameter) {
		this.parameter = parameter;
	}
	
	public CustomTestStepParameter getParameter() {
		return parameter;
	}
	
	public void setValue(String value){
		String oldValue = this.value;
		this.value = value;
		propertyChangeListeners.firePropertyChange("VALUE", oldValue, value);
	}
	public String getValue(){
		return value;
	}

	public void removeListener(PropertyChangeListener listener) {
		propertyChangeListeners.removePropertyChangeListener(listener);
	}

	public void addListener(PropertyChangeListener listener) {
		propertyChangeListeners.addPropertyChangeListener(listener);
	}
}
