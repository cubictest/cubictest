/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import org.cubictest.common.exception.CubicException;
import org.cubictest.model.AbstractPage;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.gef.controller.TestEditPart;


/**
 * Util class for model operations.
 * 
 * @author Christian Schwarz
 */
public class ModelUtil {

	/**
	 * Get the AbstractPage model object surronding an PropertyChangePart.
	 * @param editPart
	 * @return
	 */
	public static AbstractPageEditPart getSurroundingPagePart(PropertyChangePart editPart) {
		if (editPart instanceof TestEditPart) {
			return null;
		}
		if (editPart instanceof AbstractPageEditPart) {
			return (AbstractPageEditPart) editPart;
		}
		
		editPart = (PropertyChangePart) editPart.getParent();
		
		while (!(editPart.getModel() instanceof AbstractPage)) {
			editPart = (PropertyChangePart) editPart.getParent();
		}
		
		
		if (!(editPart instanceof AbstractPageEditPart)) {
			throw new CubicException("Did not find surronding AbstractPage of edit part: " + editPart);
		}
		
		return (AbstractPageEditPart) editPart;
		
	}
}
