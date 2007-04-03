/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Text;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 * Contoller for the <code>Text</code> model.
 *
 */
public class PageTextEditPart extends PageElementEditPart{

	/**
	 * Constructor for <code>PageTextEditPart</code>.
	 * @param text the model
	 */
	public PageTextEditPart(Text text) {
		super();
		setModel(text);
	}

	@Override
	protected Image getImage(boolean not) {
		String key = CubicTestImageRegistry.TEXT_IMAGE;
		return CubicTestImageRegistry.get(key,not);
	}

}
