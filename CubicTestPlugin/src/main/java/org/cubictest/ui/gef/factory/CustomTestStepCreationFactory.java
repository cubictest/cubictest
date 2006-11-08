/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.factory;

import org.cubictest.model.CustomTestStep;
import org.cubictest.pluginsupport.CustomElementLoader;
import org.eclipse.gef.requests.CreationFactory;

/**
 * @author Stein Kåre Skytteren
 * Used for creating the the different objects in the model by the tools in the Palette.
 */
public class CustomTestStepCreationFactory implements CreationFactory {

	private String className;
	private CustomElementLoader classLoader;

	public CustomTestStepCreationFactory(CustomElementLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * 
	 * @param object the class to create a new instance of 
	 */
	public CustomTestStepCreationFactory(CustomElementLoader classLoader, String className){
		this(classLoader);
		this.className = className;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public CustomTestStep getNewObject(){
		try{
			CustomTestStep customTestStep = new CustomTestStep(className);
			customTestStep.setCustomTestStepLoader(classLoader);
			return customTestStep;
		}
		catch (Exception e){
			return null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType(){
		return className;
	}


}
