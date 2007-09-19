/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	
	public String getDisplayText() {
		return file;
	}
	
	public CustomTestStep getCustomTestStep() {
		if(customTestStep == null)
			customTestStep = CustomTestStepPersistance.loadFromFile(getFile());
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
		return Arrays.asList(getCustomTestStep().getParameters().toArray());
	}
	
	public void reloadCustomTestStep(){
		customTestStep = CustomTestStepPersistance.loadFromFile(getFile());
	}
}
