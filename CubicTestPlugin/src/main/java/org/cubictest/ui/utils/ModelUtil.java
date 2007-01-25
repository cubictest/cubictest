/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import org.cubictest.common.exception.CubicException;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
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
	
	public static boolean isLegalTransition(TransitionNode sourceNode, TransitionNode targetNode, Transition transition, boolean isReconnectingSourceNode) {
		if (sourceNode.equals(targetNode))
			return false;

		if (targetNode instanceof UrlStartPoint)
			return false;
		
		if (targetNode instanceof ExtensionStartPoint)
			return false;

		if (targetNode instanceof Common)
			return false;
		
		if (targetNode == null)
			return false;

		if (sourceNode instanceof Common && !(targetNode instanceof Page))
			return false;

		
		if (isReconnectingSourceNode) {
			if (sourceNode.hasInTransition() && sourceNode.getInTransition().getStart().equals(targetNode)) {
				//cycle between source and target
				return false;
			}
		}
		else {
			//reconnecting target
			
			if (sourceNode.hasInTransition() && sourceNode.getInTransition().getStart().equals(targetNode))  {
				//cycle between source and target
				return false;
			}
			
			Transition targetInTransition = targetNode.getInTransition();
			if (targetNode.hasInTransition() && targetInTransition.getStart().equals(sourceNode))
				//duplicate transition
				return false;

			if (targetNode.hasInTransition() && !(sourceNode instanceof Common)) {
				//multiple in-transitions not allowed, unless from Common
				return false;
			}
		}
		
		return true;
	}
}
