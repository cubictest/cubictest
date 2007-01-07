/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.converters;

import org.cubictest.model.context.IContext;


/**
 * Interface for converters of an common to a list of test steps.
 * 
 * @author chr_schwarz
 */
public interface IContextConverter<T> {
	
	/**
	 * Converts an Context to a list of test steps. 
	 * The list of TestSteps should verify that all contents of the context is present on the page.
	 * @param a The context to convert.
	 */
	public PreContextHandle handlePreContext(T t,IContext a);
	public PostContextHandle handlePostContext(T t,IContext a);
}
