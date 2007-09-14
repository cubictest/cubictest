/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.converters;

import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.context.IContext;


/**
 * Interface for converters of an common to a list of test steps.
 * 
 * @author chr_schwarz
 */
public interface IContextConverter<T extends IResultHolder> {
	
	/**
	 * Handle entry into a new context.
	 */
	public PreContextHandle handlePreContext(T t,IContext a);
	
	
	/**
	 * Handle exit from context.
	 */
	public PostContextHandle handlePostContext(T t,IContext a);
}
