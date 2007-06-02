/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class CustomTestStep extends ConnectionPoint {

	private transient ICustomTestStep customTestStep;
	private HashMap<String, String> config = new HashMap<String, String>();
	private String file;
	
	public CustomTestStep(String file) {
		super();
		config.put("test", "bleh");
		config.put("asdf", "qwer");
		this.file = file;
	}
	
	public String getDisplayText() {
		return getCustomTestStep().getDisplayText();
	}
	
	private CustomTestStep getCustomTestStep() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String,String> getConfig() {
		return config;
	}

	public List<String> getArgumentNames() {
		if(customTestStep.getArgumentNames() != null) {
			return customTestStep.getArgumentNames();
		}
		return new ArrayList<String>();
	}

	public IFile getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
