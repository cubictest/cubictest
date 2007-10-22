/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.formElement;

/**
 * A password field on the page.
 * 
 * @author SK Skytteren
 *
 */
public class Password extends AbstractTextInput {

	@Override
	public String getType() {
		return "Password";
	}

}
