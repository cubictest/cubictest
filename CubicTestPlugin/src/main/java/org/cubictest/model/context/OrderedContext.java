/*
 * Created on 11.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model.context;


public class OrderedContext extends AbstractContext {

	@Override
	public String getDescription() {
		return "ORDERED_CONTEXT";
	}
	
	@Override
	public String getType() {
		return "OrderedContext";
	}
}
