package org.cubictest.model.customstep;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class CustomTestStepParameterList {	
	public static final String ADD = "ADD";
	public static final String NEW = "NEW";
	public static final String DELETE = "DELETE";
	

	private List<CustomTestStepParameter> parameters = new ArrayList<CustomTestStepParameter>();

	private transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
	
	public void remove(Object obj) {
		parameters.remove(obj);
		propertyChangeListeners.firePropertyChange(DELETE, obj, null);
		if(obj instanceof CustomTestStepParameter)
			for(PropertyChangeListener listener : propertyChangeListeners.getPropertyChangeListeners())
				((CustomTestStepParameter)obj).removeListener(listener);
	}

	public void add(CustomTestStepParameter parameter) {
		parameters.add(parameter);
		propertyChangeListeners.firePropertyChange(ADD, null, parameter);
		for(PropertyChangeListener listener : propertyChangeListeners.getPropertyChangeListeners())
			parameter.addListener(listener);
	}

	public void addChangeListener(PropertyChangeListener listener) {
		if(propertyChangeListeners == null)
			propertyChangeListeners = new PropertyChangeSupport(this);
		propertyChangeListeners.addPropertyChangeListener(listener);
		for(CustomTestStepParameter parameter: parameters){
			parameter.addListener(listener);
		}
	}
	public void removeChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.removePropertyChangeListener(listener);
		for(CustomTestStepParameter parameter: parameters){
			parameter.removeListener(listener);
		}
	}

	public boolean isAvailableKey(String key) {
		if(key == null)
			return false;
		for(CustomTestStepParameter parameter: parameters){
			if(key.equals(parameter.getKey()))
				return false;
		}
		return true;
	}

	public CustomTestStepParameter createNewParameter() {
		String key = "key";  //  @jve:decl-index=0:
		int i = 0;
		while(!isAvailableKey(key)){
			key = "key" + ++i;
		}
		CustomTestStepParameter parameter = new CustomTestStepParameter(key,
				"Your Description for " + key);
		parameters.add(parameter);
		for(PropertyChangeListener listener : propertyChangeListeners.getPropertyChangeListeners())
			parameter.addListener(listener);
		propertyChangeListeners.firePropertyChange(NEW, null, parameter);
		return parameter;
	}

	public Object[] toArray() {
		return parameters.toArray();
	}

}
