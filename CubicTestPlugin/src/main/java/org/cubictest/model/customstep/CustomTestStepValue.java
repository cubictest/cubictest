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
