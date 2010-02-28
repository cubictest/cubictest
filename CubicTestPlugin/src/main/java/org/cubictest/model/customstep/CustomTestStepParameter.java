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

public class CustomTestStepParameter {

	private static final String DESCRIPTION = "DESCRIPRTION";
	private static final String KEY = "KEY";
	
	private String description;
	private String key;
	private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public CustomTestStepParameter(String key, String description) {
		this.key = key;
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		String oldKey = this.key;
		this.key = key;
		listeners.firePropertyChange(KEY,oldKey,key);
	}
	
	public void setDescription(String descripton) {
		String oldDescription = this.description;
		this.description = descripton;
		listeners.firePropertyChange(DESCRIPTION, oldDescription, descripton);
	}

	public String getDescription() {
		return description;
	}

	public void addListener(PropertyChangeListener listener) {
		if(listeners == null)
			listeners = new PropertyChangeSupport(this);
		listeners.addPropertyChangeListener(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof CustomTestStepParameter)) {
			return false;
		}
		CustomTestStepParameter otherParam = (CustomTestStepParameter) other;
		return (key + description).equals(otherParam.getKey() + otherParam.getDescription());
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
