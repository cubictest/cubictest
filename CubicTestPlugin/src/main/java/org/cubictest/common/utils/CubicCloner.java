/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *    Mao YE - version up, new feature extended
 *******************************************************************************/
package org.cubictest.common.utils;

import java.lang.reflect.Method;

import org.cubictest.persistence.CubicTestXStream;
import org.eclipse.core.resources.IProject;


/**
 * Creates a deep copy of an object using XStream.
 * 
 * @author Christian Schwarz
 */
public class CubicCloner {

	public static CubicTestXStream xStream;
	
	/**
	 * Returns a deep copy of an object using XStream.
	 */
	public static Object deepCopy(Object oldObj) {

		if (xStream == null) {
			xStream = new CubicTestXStream();
		}
		String xml = xStream.toXML(oldObj);
		Object newObj = xStream.fromXML(xml);
		
		//check if the variable "project" can be set (typically transient, and thus not copied by XStream):
		IProject project = null;
		try {
			Method getProject = oldObj.getClass().getMethod("getProject", null);
			if (getProject != null) {
				project = (IProject) getProject.invoke(oldObj, null);
			}
			Method setProject = oldObj.getClass().getMethod("setProject", new Class[] {IProject.class});
			if (setProject != null) {
				setProject.invoke(newObj, project);
			}
		} catch (Exception e) {
			//ignore
		}
		return newObj;
	}
}
