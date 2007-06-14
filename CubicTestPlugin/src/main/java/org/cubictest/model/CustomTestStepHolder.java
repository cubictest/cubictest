/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.persistence.CustomTestStepPersistance;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

public class CustomTestStepHolder extends ConnectionPoint {

	private transient CustomTestStep customTestStep;
	private HashMap<String, String> config = new HashMap<String, String>();
	private String file;
	private transient IProject project;
	
	public CustomTestStepHolder(String file, IProject project) {
		super();
		this.project = project;
		this.file = file;
		config.put("test", "bleh");
		config.put("asdf", "qwer");
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

	public HashMap<String,String> getConfig() {
		return config;
	}

	public List<String> getArgumentNames() {
		if(getCustomTestStep().getArgumentNames() != null) {
			return customTestStep.getArgumentNames();
		}
		return new ArrayList<String>();
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
