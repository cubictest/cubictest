package org.cubictest.model.customstep;

import org.cubictest.model.ConnectionPoint;
import org.eclipse.core.resources.IProject;

public class CustomStepNode extends ConnectionPoint {

	private final IProject project;
	private final String filePath;

	public CustomStepNode(String filePath, IProject project) {
		this.filePath = filePath;
		this.project = project;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public IProject getProject() {
		return project;
	}

}
