/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.persistence.CustomTestStepPersistance;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

public class CustomTestStepHolder extends ConnectionPoint {

	private transient CustomTestStep customTestStep;
	private Map<CustomTestStepParameter, String> config = 
		new HashMap<CustomTestStepParameter, String>();
	private String file;
	private transient IProject project;
	
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
	
	private CustomTestStep getCustomTestStep() {
		if(customTestStep == null)
			customTestStep = CustomTestStepPersistance.loadFromFile(getFile());
		return customTestStep;
	}

	public Map<CustomTestStepParameter,String> getConfig() {
		return config;
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
}
