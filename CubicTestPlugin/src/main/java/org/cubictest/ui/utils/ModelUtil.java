/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import org.cubictest.common.utils.UserInfo;
import org.cubictest.model.Common;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;


/**
 * Util class for model operations.
 * 
 * @author Christian Schwarz
 */
public class ModelUtil {

	public static int TRANSITION_EDIT_NO_CHANGES = 0;
	public static int TRANSITION_EDIT_VALID = 2;
	public static int TRANSITION_EDIT_NOT_VALID = 4;

	
	public static int isLegalTransition(TransitionNode sourceNode, TransitionNode targetNode, 
			boolean isReconnectingSourceNode, boolean isReconnectingTargetNode) {
		
		boolean isNewTransition = !isReconnectingSourceNode && !isReconnectingTargetNode;
		
		if (sourceNode == null)
			return TRANSITION_EDIT_NOT_VALID;
		
		if (sourceNode.equals(targetNode))
			return TRANSITION_EDIT_NOT_VALID;

		if (targetNode instanceof UrlStartPoint)
			return TRANSITION_EDIT_NOT_VALID;
		
		if (targetNode instanceof ExtensionStartPoint)
			return TRANSITION_EDIT_NOT_VALID;

		if (targetNode instanceof Common)
			return TRANSITION_EDIT_NOT_VALID;
		
		if (targetNode == null)
			return TRANSITION_EDIT_NOT_VALID;

		if (sourceNode instanceof Common && !(targetNode instanceof Page))
			return TRANSITION_EDIT_NOT_VALID;

		if (sourceNode instanceof IStartPoint && (isReconnectingSourceNode || isNewTransition)) {
			if (sourceNode.getOutTransitions().size() >= 1) { 
				UserInfo.showInfoDialog("Only one out-transition is allowed from startpoints.\n" +
				"Hint: Create a new test or a tree after the first page/state.");
				return TRANSITION_EDIT_NOT_VALID;
			}
		}
		
		if (isReconnectingSourceNode) {
			if (sourceNode.hasInTransition() && sourceNode.getInTransition().getStart().equals(targetNode)) {
				//cycle between source and target
				UserInfo.showInfoDialog("Cycles are not allowed.\n" +
						"Hint: Create a new page/state instead.");
				return TRANSITION_EDIT_NOT_VALID;
			}
		}
		else {
			//reconnecting target
			
			if (sourceNode.hasInTransition() && sourceNode.getInTransition().getStart().equals(targetNode))  {
				//cycle between source and target
				UserInfo.showInfoDialog("Cycles are not allowed.\n" +
						"Hint: Create a new page/state instead.");
				return TRANSITION_EDIT_NOT_VALID;
			}

			if (targetNode.hasInTransition() && !(sourceNode instanceof Common)) {
				boolean isNoChanges = targetNode.getInTransition().getStart().equals(sourceNode);
				if (isNoChanges) {
					//ok, user has just reconnected to the original state
					return TRANSITION_EDIT_NO_CHANGES;
				}
				else {
					//multiple in-transitions not allowed, unless from Common
					UserInfo.showInfoDialog("Multiple in-connections to a page are not allowed, unless from a Common.\n" +
							"Hint: Create a new page/state instead.");
					return TRANSITION_EDIT_NOT_VALID;
				}
			}
			
			Transition targetInTransition = targetNode.getInTransition();
			if (targetNode.hasInTransition() && targetInTransition.getStart().equals(sourceNode))
				//duplicate transition
				return TRANSITION_EDIT_NOT_VALID;


		}
		
		return TRANSITION_EDIT_VALID;
	}


	public static boolean assertHasOnlyOnePathFrom(TransitionNode node) {
		if (node == null) {
			return true;
		}
		int nextNodes = 0;
		for (Transition transition : node.getOutTransitions()) {
			if (!(transition.getEnd() instanceof ExtensionPoint)) {
				nextNodes++;
			}
		}
		
		if (nextNodes == 0) {
			return true;
		}
		else if(nextNodes == 1) {
			return assertHasOnlyOnePathFrom(node.getOutTransitions().get(0).getEnd());
		}
		else {
			return false;
		}
	}
	
	public static boolean isTestFile(String fileName) {
		if (fileName.endsWith(".aat") || fileName.endsWith(".ats")) {
			return true;
		}
		return false;
	}
}
