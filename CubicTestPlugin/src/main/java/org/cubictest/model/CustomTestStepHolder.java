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
package org.cubictest.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepValue;
import org.cubictest.persistence.CustomTestStepPersistance;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

public class CustomTestStepHolder extends ConnectionPoint implements ICustomTestStepHolder{

	private transient CustomTestStep customTestStep;
	private String file;
	private transient IProject project;
	private HashMap<CustomTestStepParameter, CustomTestStepValue> values;
	
	public CustomTestStepHolder(String file, IProject project) {
		super();
		this.project = project;
		this.file = file;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public String getName() {
		String stepName = getCustomTestStep(false).getName();
		if (StringUtils.isNotBlank(stepName)) {
			return stepName;
		}
		else {
			return file;
		}
	}
	
	public String getDescription() {
		String desc = getCustomTestStep(false).getDescription();
		if (desc == null) {
			return "";
		}
		return desc;
	}
	
	public CustomTestStep getCustomTestStep(boolean forceRefreshFile) {
		if(customTestStep == null || forceRefreshFile)
			reloadCustomTestStep();
		return customTestStep;
	}

	public String getFilePath() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public IFile getFile() {
		return project.getFile(new Path(file));
	}
	
	public CustomTestStepValue getValue(CustomTestStepParameter parameter){
		if(values == null)
			values = new HashMap<CustomTestStepParameter,CustomTestStepValue>();
		CustomTestStepValue value = values.get(parameter);
		if(value == null){
			value = new CustomTestStepValue(parameter);
			values.put(parameter, value);
		}
		return value;
	}
	
	public List<CustomTestStepParameter> getCustomTestStepParameters(){
		return Arrays.asList(getCustomTestStep(false).getParameters().toArray());
	}
	
	public void reloadCustomTestStep(){
		CustomTestStep oldStep = customTestStep;
		customTestStep = CustomTestStepPersistance.loadFromFile(getFile());
		firePropertyChange(CUSTOMSTEP, oldStep, customTestStep);
	}
	
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
	}
}
