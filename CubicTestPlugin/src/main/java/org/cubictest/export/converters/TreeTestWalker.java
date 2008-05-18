/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.export.converters;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteractionsTransition;

/**
 * Converts a Test using generic handlers and a generic result holder.
 * Supports tree tests. 
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 * 
 */
public class TreeTestWalker<T extends IResultHolder> {

	private List<TransitionNode> convertedNodes = new ArrayList<TransitionNode>();

	private Class<? extends IUrlStartPointConverter<T>> urlStartPointConverter;

	private PageWalker<T> pageWalker;

	private Class<? extends ITransitionConverter<T>> transitionConverter;

	private Class<? extends ICustomTestStepConverter<T>> customTestStepConverter;

	
	/**
	 * Public constructor. Accepts the generic converters.
	 */
	public TreeTestWalker(Class<? extends IUrlStartPointConverter<T>> urlSpc,
			Class<? extends IPageElementConverter<T>> pec,
			Class<? extends IContextConverter<T>> cc,
			Class<? extends ITransitionConverter<T>> tc,
			Class<? extends ICustomTestStepConverter<T>> ctsc) {
		this.urlStartPointConverter = urlSpc;
		this.pageWalker = new PageWalker<T>(pec,cc);
		this.transitionConverter = tc;
		this.customTestStepConverter = ctsc;
	}

	
	/**
	 * Traverse the test, using the generic converters.
	 * Converts all paths in test (tree).
	 */
	public void convertTest(Test test, T resultHolder) {
		
		//If multiple paths in tree test, we should start from the very beginning for each path.
		//As we use extension points etc., we do not know in advance how many times to process a node.
		//We therefore use a strategy of looping over the tree many times and maintaing a list of converted nodes.
		//See JUnit test case.
		
		try {
			//be sure to cover all paths in tree test:
			for (int path = 0; path < 42; path++) {
				convertTransitionNode(resultHolder, test.getStartPoint(), null, null);
			}
		} 
		catch (IllegalAccessException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		catch (InstantiationException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}

	
	/**
	 * Traverse the test, using the generic converters.
	 * Stops at target page. Does not convert all paths in tree if target page non-null. 
	 */
	public void convertTest(Test test, T resultHolder, Page targetPage) {
		convertTest(test, null, resultHolder, targetPage);
	}
	
	
	/**
	 * Traverse the test, using the generic converters.
	 * Converts only path in test that leads to the targetExtensionPoint, if non-null.
	 * Stops at target page, if non-null.
	 * 
	 * @param test
	 * @param resultHolder
	 * @param targetExtensionPoint Convert only path in test that leads to this targetExtensionPoint
	 * @param targetPage page that the walker should stop when it gets to
	 */
	public void convertTest(Test test, ConnectionPoint targetExtensionPoint, T resultHolder, Page targetPage) {
		if (targetExtensionPoint == null && targetPage == null) {
			//traverse tree
			convertTest(test, resultHolder);
			return;
		}
		
		//we have a target; do not traverse tree
		try {
			convertTransitionNode(resultHolder, test.getStartPoint(), targetExtensionPoint, targetPage);
		} 
		catch (IllegalAccessException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		catch (InstantiationException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	
	/**
	 * Converts transition node and its successors. If targetExtensionPoint is non-null,
	 * only converts test on path to that ExtensionPoint. If node is subtest,
	 * only convert the used path in the subtest (the path leading to the
	 * extension point that is extended from).
	 * Stops at targetPage if non-null.
	 * 
	 * @param resultHolder object holding results.
	 * @param node the node to convert.
	 * @param targetExtensionPoint if non-null, only convert node on path to this ExtensionPoint.
	 * @param targetPage page that the walker should stop when it gets to
	 * @return whether the node is finished (all paths in tree containing node has been traversed).
	 */
	protected boolean convertTransitionNode(T resultHolder, TransitionNode node, ConnectionPoint targetExtensionPoint, Page targetPage)
			throws InstantiationException, IllegalAccessException {

		boolean nodeFinished = true; //init

		//If multiple paths in tree test, we should start from the very beginning for each path.
		//As we use extension points etc., we do not know in advance how many times to process a node.
		//We therefore use a strategy of looping over the tree many times and maintaing a list of converted nodes.
		//See JUnit test case.
		if (convertedNodes.contains(node)) {
			return true;
		}

		if (nodeShouldBeConverted(node, targetExtensionPoint, targetPage)) {
			
			if (node instanceof UrlStartPoint) {
				resultHolder.pushBreadcrumb(node);
				urlStartPointConverter.newInstance().handleUrlStartPoint(resultHolder, (UrlStartPoint) node, targetExtensionPoint == null);
				resultHolder.popBreadcrumb();
			} 
			else if (node instanceof ExtensionStartPoint) {
				resultHolder.pushBreadcrumb(node);
				handleExtensionStartPoint(resultHolder, node);
				resultHolder.popBreadcrumb();
			}
			else if (node instanceof SubTest) {
				resultHolder.pushBreadcrumb(node);
				handleSubTest(resultHolder, node, targetExtensionPoint);
				resultHolder.popBreadcrumb();
			}
			else if (node instanceof Page) {
				resultHolder.pushBreadcrumb(node);
				pageWalker.handlePage(resultHolder, (Page) node);
				resultHolder.popBreadcrumb();
			} 
			else if (node instanceof CustomTestStepHolder) {
				resultHolder.pushBreadcrumb(node);
				handleCustomStep(resultHolder, node);
				resultHolder.popBreadcrumb();
			}

			//OK, node converted, see if we should stop traversing the successor nodes:
			if (node.equals(targetPage)) {
				return true;
			}
			
			//convert the successor nodes (depth first, starts recursive converting down each path):
			int pathNum = 0;
			for (Transition outTransition : node.getOutTransitions()) {
				pathNum++;
				nodeFinished = handleNextNode(resultHolder, node, targetExtensionPoint, targetPage, pathNum, outTransition);
				if (!nodeFinished) {
					//skip to (start from) *root* of tree before traversing the next path
					break;
				}
			}
			
			//possible override of nodeFinished:
			if (targetExtensionPoint != null) {
				//there can exists a tree test *after* this test, so we do not know whether we are finished
				nodeFinished = false;
			}
			
			if (nodeFinished) {
				convertedNodes.add(node);
			}
		}
		return nodeFinished;
	}



	private boolean handleNextNode(T resultHolder, TransitionNode node,
			ConnectionPoint targetExtensionPoint, Page targetPage,
			int pathNum, Transition outTransition)
			throws InstantiationException, IllegalAccessException {
		
		boolean nodeFinished = true; //init
		TransitionNode endNode = (TransitionNode) outTransition.getEnd();

		if (convertedNodes.contains(endNode)) {
			//do not convert;
		}
		else {
			if (nodeShouldBeConverted(endNode, targetExtensionPoint, targetPage)) {
				
				if (outTransition instanceof UserInteractionsTransition) {
					//convert user interactions transition:
					transitionConverter.newInstance().handleUserInteractions(resultHolder,(UserInteractionsTransition) outTransition);
				}
				else {
					//normal connection, only follow it
				}
				
				// convert end node recursively: 
				nodeFinished = convertTransitionNode(resultHolder, endNode, targetExtensionPoint, targetPage);

				if (pathNum < node.getOutTransitions().size() && targetExtensionPoint == null) {
					//end node converted, skip to root of tree to start traversal of the other paths from there.
					nodeFinished = false;
				}
			}
		}
		return nodeFinished;
	}



	private void handleCustomStep(T resultHolder, TransitionNode node) throws InstantiationException, IllegalAccessException {
		
		CustomTestStepHolder ctsh = (CustomTestStepHolder) node;
		ICustomTestStepConverter<T> ctsc = customTestStepConverter.newInstance();
		ctsc.handleCustomStep(resultHolder,
				ctsh, ctsc.getDataKey() == null ? null : ctsh.getCustomTestStep().getData( ctsc.getDataKey()));
	}



	private void handleSubTest(T resultHolder, TransitionNode node, ConnectionPoint targetExtensionPoint) {
		
		SubTest subtest = (SubTest) node;
		Test subtestTest = subtest.getTest(true);
		List<Transition> outTransitions = node.getOutTransitions();
		ExtensionPoint targetExPoint = null;
		if (outTransitions != null && outTransitions.size() > 0 && outTransitions.get(0) instanceof ExtensionTransition) {
			// we have a target
			targetExPoint = ((ExtensionTransition) outTransitions.get(0)).getExtensionPoint();
		}
		else {
			// no target exPoint. Check that only one path in sub test (if one path, then ok)
			if(!ModelUtil.hasOnlyOnePathFromNodeToEndOfTest(subtestTest.getStartPoint())) {
				ErrorHandler.logAndShowErrorDialogAndThrow("Error traversing subtest: The \"" + subtest.getFileName() + "\" subtest " +
						"is a tree (has more than one path in it) and an extension point is not used. The exporter does not know which path to execute.\n\n" +
						"To fix, create an extension point in the test, and extend from it where the subtest is used.");
			}
		}
		
		// Parameterization: Set correct parameter in sub test:
		if (subtest.hasOwnParams()) {
			subtestTest.getParamList().setParameterIndex(subtest.getParameterIndex());
			subtestTest.updateObservers();
		}
		// i18n: Set correct language in sub test:
		if (subtest.hasOwnLanguage()) {
			subtestTest.getAllLanguages().setCurrentLanguage(subtest.getLanguage());
			subtestTest.updateObservers();
		}
		
		// Convert sub test:
		try {
			convertTransitionNode(resultHolder, subtestTest.getStartPoint(), targetExPoint, null);
			resultHolder.updateStatus(subtest, false, targetExtensionPoint);
		}
		catch (Exception e) {
			handleSubTestException(resultHolder, subtest, targetExtensionPoint, e);
		}
	}



	private void handleExtensionStartPoint(T resultHolder, TransitionNode node) throws InstantiationException, IllegalAccessException {
		ExtensionStartPoint exStartPoint = (ExtensionStartPoint) node;
		Test subtestTest = (((SubTest) node).getTest(true));
		ExtensionPoint targetInSubTest = ((ExtensionTransition) exStartPoint.getOutTransitions().get(0)).getExtensionPoint();
		convertTransitionNode(resultHolder, subtestTest.getStartPoint(), targetInSubTest, null);
		resultHolder.updateStatus(((SubTest) node), false, (ExtensionStartPoint) node);
	}



	private boolean nodeShouldBeConverted(TransitionNode node, ConnectionPoint targetExtensionPoint, Page targetPage) {
		if (node == null) {
			Logger.warn("Encountered null node in traversal of test. Skipping it");
			return false;
		}
		else if (targetExtensionPoint == null && targetPage == null) {
			return true;
		}
		else if (targetPage == null) { 
			return targetExtensionPoint == null || ModelUtil.isOnPathToNode(node, targetExtensionPoint);
		}
		else if (targetExtensionPoint == null) {
			return targetPage == null || ModelUtil.isOnPathToNode(node, targetPage);
		}
		return false;
	}


	private void handleSubTestException(T resultHolder, SubTest subTest, ConnectionPoint targetExtensionPoint, Exception e) {
		if (e instanceof AssertionFailedException) {
			resultHolder.updateStatus(subTest, false, targetExtensionPoint);
			if (shouldThrowException(resultHolder, subTest)) {
				throw new AssertionFailedException(e.getMessage() + ", in subtest \"" + subTest.getFileName() + "\"");
			}
		}
		else if (e instanceof ExporterException) {
			resultHolder.updateStatus(subTest, true, targetExtensionPoint);
			if (shouldThrowException(resultHolder, subTest)) {
				throw new ExporterException(e.getMessage() + ", in subtest \"" + subTest.getFileName() + "\"");
			}
		}
		else if (e instanceof Exception) {
			resultHolder.updateStatus(subTest, true, targetExtensionPoint);
			ErrorHandler.logAndRethrow(e);
		}
	}


	private boolean shouldThrowException(IResultHolder resultHolder, TransitionNode node) {
		if (resultHolder.shouldFailOnAssertionFailure()) {
			return true;
		}
		else {
			//throw if not a test suite
			return !(ModelUtil.getStartPoint(node) instanceof TestSuiteStartPoint);
		}
	}
	
}
