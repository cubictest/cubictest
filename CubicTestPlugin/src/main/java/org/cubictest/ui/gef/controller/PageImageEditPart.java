/*
 * Created on 12.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Image;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;

/**
 * Controller for Image model.
 * @author chr_schwarz
 */
public class PageImageEditPart extends PageElementEditPart {
	
	public PageImageEditPart(Image image) {
		setModel(image);
	}
	
	@Override
	protected org.eclipse.swt.graphics.Image getImage(boolean not) {
		String key = CubicTestImageRegistry.IMAGE_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}
}
