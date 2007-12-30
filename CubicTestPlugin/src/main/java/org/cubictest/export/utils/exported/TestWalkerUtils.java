/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.utils.exported;

import org.cubictest.common.exception.CubicException;
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
	 * Checks if there is a way to get from the node to check to the target node
	 * @param nodeToCheck the node to check all routes from
	 * @param targetNode the node we want to be able to reach
	 */
	public static boolean isOnPathToNode(TransitionNode nodeToCheck, TransitionNode targetNode) {

		String targetPageId = "";
		
		if (targetNode instanceof ExtensionStartPoint) {
			targetPageId = ((ExtensionStartPoint) targetNode).getSourceExtensionPointPageId();
		} 
		else if (targetNode instanceof ExtensionPoint) {
			targetPageId = ((ExtensionPoint) targetNode).getPageId();
		}
		else if (targetNode instanceof Page) {
			targetPageId = ((Page) targetNode).getId();
		}
		else {
			throw new CubicException("Unsupported connection point: "
					+ targetNode.toString());
		}

		if (nodeToCheck instanceof Page) {
			if (nodeToCheck.getId().equals(targetPageId)) {
				return true;
			}
		}
		
		//recursively check all end nodes and check if we can get to the target node:
		for (Transition outTransition : nodeToCheck.getOutTransitions()) {
			TransitionNode endNode = (TransitionNode) outTransition.getEnd();
			if (isOnPathToNode(endNode, targetNode)) {
				return true;
			}
		}
		return false;
	}
}
