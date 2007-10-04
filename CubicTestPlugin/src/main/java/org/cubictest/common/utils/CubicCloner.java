/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
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
