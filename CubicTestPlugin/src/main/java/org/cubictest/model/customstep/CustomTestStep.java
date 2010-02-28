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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.model.customstep.data.CustomTestStepData;

public class CustomTestStep {

	private Map<String, CustomTestStepData> customSteps = new HashMap<String, CustomTestStepData>();
	private String name = "";
	private String description = "";
	private transient List<ICustomStepListener> listeners = new ArrayList<ICustomStepListener>();
	private CustomTestStepParameterList parameters;
	public static final String DESCRIPTION_CHANGED = "DescriptionChanged";
	public static final String NAME_CHANGED = "NameChanged";
	
	public CustomTestStepData getData(String key) {
		CustomTestStepData customStep = customSteps.get(key);
		if(customStep == null){
			customStep = new CustomTestStepData();
			customSteps.put(key,customStep);
		}
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
		if(listeners == null)
			listeners = new ArrayList<ICustomStepListener>();
		listeners.add(listener);
	}
	
	public void removeCustomStepListener(ICustomStepListener listener) {
		if(listeners == null)
			listeners = new ArrayList<ICustomStepListener>();
		listeners.remove(listener);
	}
	
	private void firePropertyChange(String key, Object oldValue, Object newValue) {
		CustomTestStepEvent event = new CustomTestStepEvent(key,oldValue,newValue);
		if(listeners == null)
			listeners = new ArrayList<ICustomStepListener>();
		for(ICustomStepListener listener : listeners)
			listener.handleEvent(event);
	}

	public CustomTestStepParameterList getParameters() {
		if(parameters == null)
			parameters = new CustomTestStepParameterList();
		return parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(NAME_CHANGED, oldName, name);
	}
}
