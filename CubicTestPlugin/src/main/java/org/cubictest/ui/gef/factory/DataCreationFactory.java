/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.factory;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.gef.requests.CreationFactory;

/**
 * Used for creating the the different objects in the model by the tools in the Palette.
 * 
 * @author SK Skytteren
 */
public class DataCreationFactory implements CreationFactory {

	private Object clazz;

	/**
	 * 
	 * @param object the class to create a new instance of 
	 */
	public DataCreationFactory(Object object){
		this.clazz = object;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject(){
		try{
			return ((Class) clazz).newInstance();
		}
		catch (Exception e){
			ErrorHandler.logAndThrow("Error creating object of class " + clazz);
			return null; //unreachable
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType(){
		return clazz;
	}


}
