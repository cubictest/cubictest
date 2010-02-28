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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CustomTestStepValue {
	
	private CustomTestStepParameter parameter;
	private String value = "";
	
	private transient PropertyChangeSupport propertyChangeListeners;

	public CustomTestStepValue(CustomTestStepParameter parameter) {
		this.parameter = parameter;
	}
	
	public CustomTestStepParameter getParameter() {
		return parameter;
	}
	
	public void setValue(String value){
		String oldValue = this.value;
		this.value = value;
		getPropertyChangeListeners().firePropertyChange("VALUE", oldValue, value);
	}
	public String getValue(){
		return value;
	}

	public void removeListener(PropertyChangeListener listener) {
		getPropertyChangeListeners().removePropertyChangeListener(listener);
	}

	public void addListener(PropertyChangeListener listener) {
		getPropertyChangeListeners().addPropertyChangeListener(listener);
	}

	private PropertyChangeSupport getPropertyChangeListeners() {
		if (propertyChangeListeners == null) {
			propertyChangeListeners = new PropertyChangeSupport(this);
		}
		return propertyChangeListeners;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof CustomTestStepValue)) {
			return false;
		}
		CustomTestStepValue otherValue = (CustomTestStepValue) other;
		return value.equals(otherValue.getValue());
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
