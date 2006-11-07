/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cubictest.custom.ICustomTestStep;
import org.cubictest.custom.IElementContext;
import org.cubictest.pluginsupport.CustomElementLoader;
import org.cubictest.pluginsupport.interfaces.IClassChangeListener;
import org.cubicunit.Document;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;

public class CustomTestStep extends ConnectionPoint implements IClassChangeListener {

	private transient ICustomTestStep customTestStep;
	private transient CustomElementLoader customTestStepLoader;
	private HashMap<String, String> config = new HashMap<String, String>();
	
	private String customTestClass;
	
	public CustomTestStep(String clazz) {
		super();
		config.put("test", "bleh");
		config.put("asdf", "qwer");
		this.customTestClass = clazz;
	}
	
	public String getCustomTestClass() {
		return customTestClass;
	}
	
	public IFile getClassSrcPath() {
		return customTestStepLoader.getClassSrcPath(customTestClass);
	}
	
	public void setCustomTestClass(String className) {
		this.customTestClass = customTestStepLoader.cleanClassName(className);
	}
	
	public String getDisplayText() {
		try {
			return getCustomTestStep().getDisplayText();
		} catch (ClassNotFoundException e) {
			return "Class not found";
		}
	}
	
	public void execute(IElementContext context, Document document) {
		try {
			getCustomTestStep().execute(config, context, document);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private ICustomTestStep getCustomTestStep() throws ClassNotFoundException {
		if(customTestStep == null) {
			try {
				customTestStep = customTestStepLoader.newInstance(customTestClass);
			} catch (ClassNotFoundException e) {
				customTestStepLoader.createClass(customTestClass, "ICustomTestStep");
				customTestStep = customTestStepLoader.newInstance(customTestClass);
			}
		}
		return customTestStep;
	}

	public void classChanged() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				firePropertyChange(NAME,null,null);
			}
		});
	}

	public void setCustomTestStepLoader(CustomElementLoader customTestStepLoader) {
		this.customTestStepLoader = customTestStepLoader;
		customTestStepLoader.addClassChangeListener(this);
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
}
