package org.cubictest.model.customstep;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
	
}
