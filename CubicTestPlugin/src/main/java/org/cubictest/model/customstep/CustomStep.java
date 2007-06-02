package org.cubictest.model.customstep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.model.customstep.data.CustomTestStepData;

public class CustomStep {

	private Map<String, CustomTestStepData> customSteps = new HashMap<String, CustomTestStepData>();
	private String description;
	private List<ICustomStepListener> listeners = new ArrayList<ICustomStepListener>();
	private static final String DESCRIPTION_CHANGED = "DescriptionChanged";
	
	public CustomTestStepData getData(String key) {
		CustomTestStepData customStep = customSteps.get(key);
		if(customStep == null)
			customStep = new CustomTestStepData();
		return customStep;
	}
	
	public String getDescription(){
		return description;
	}

	public void setDescription(String description) {
		String oldDescription = this.description;
		this.description = description;
		firePropertyChange(DESCRIPTION_CHANGED, oldDescription, description);
	}

	public void addCustomStepListener(ICustomStepListener listener) {
		listeners.add(listener);
	}
	
	private void firePropertyChange(String key, Object oldValue, Object newValue) {
		CustomTestStepEvent event = new CustomTestStepEvent(key,oldValue,newValue);
		for(ICustomStepListener listener : listeners)
			listener.handleEvent(event);
	}
}
