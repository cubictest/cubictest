/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.factory;

import org.eclipse.gef.requests.CreationFactory;

/**
 * @author Stein Kåre Skytteren
 * Used for creating the the different objects in the model by the tools in the Palette.
 */
public class DataCreationFactory implements CreationFactory {

	private Object object;

	/**
	 * 
	 * @param object the class to create a new instance of 
	 */
	public DataCreationFactory(Object object){
		this.object = object;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject(){
		try{
			return ((Class) object).newInstance();
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
		return object;
	}


}
