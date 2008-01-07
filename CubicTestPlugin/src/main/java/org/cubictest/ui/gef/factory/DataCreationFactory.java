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
			return ((Class<?>) clazz).newInstance();
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
