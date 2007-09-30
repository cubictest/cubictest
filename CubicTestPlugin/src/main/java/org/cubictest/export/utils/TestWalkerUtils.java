/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.utils;

import org.cubictest.common.exception.CubicException;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;

/**
 * Util class for TestWalker.
 * 
 * @author chr_schwarz
 *
 */
public class TestWalkerUtils {

	
	/**
	 * Checks whether a node is on the path to the targeted extension point.
	 */
	public static boolean isOnExtensionPointPath(TransitionNode node, ConnectionPoint targetExtensionPoint) {

		String targetPageId = "";
		
		if (targetExtensionPoint instanceof ExtensionStartPoint) {
			targetPageId = ((ExtensionStartPoint) targetExtensionPoint).getSourceExtensionPointPageId();
		} 
		else if (targetExtensionPoint instanceof ExtensionPoint) {
			targetPageId = ((ExtensionPoint) targetExtensionPoint).getPageId();
		}
		else {
			throw new CubicException("Unsupported connection point: "
					+ targetExtensionPoint.toString());
		}

		
		if (node instanceof Page) {
			if (node.getId().equals(targetPageId)) {
				return true;
			}
		}
		
		//recursively check all end nodes:
		for (Transition outTransition : node.getOutTransitions()) {
			TransitionNode endNode = (TransitionNode) outTransition.getEnd();
			if (isOnExtensionPointPath(endNode, targetExtensionPoint)) {
				return true;
			}
		}
		return false;
	}
}
