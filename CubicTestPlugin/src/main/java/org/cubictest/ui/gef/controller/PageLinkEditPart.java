/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Link;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 * Contoller for the <code>Link</code> model.
 *
 */
public class PageLinkEditPart extends PageElementEditPart{

	/**
	 * Constructor for <code>PageLinkEditPart</code>.
	 * @param link the model
	 */
	public PageLinkEditPart(Link link) {
		setModel(link);	
	}	
	
	@Override
	protected Image getImage(boolean not) {
		String key = CubicTestImageRegistry.LINK_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}
	
	@Override
	public Link getModel() {
		return (Link) super.getModel();
	}
}
