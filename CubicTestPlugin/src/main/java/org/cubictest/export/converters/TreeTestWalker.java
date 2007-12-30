/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.export.converters;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.exception.UnknownExtensionPointException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.holders.IResultHolder;
import org.cubictest.export.utils.exported.TestWalkerUtils;
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
import org.cubictest.ui.utils.ModelUtil;

/**
 * Converts a Test into test steps placed in the specified resultholder. 
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 * 
 */
public class TreeTestWalker<T extends IResultHolder> {

	private List<TransitionNode> convertedNodes = new ArrayList<TransitionNode>();

	private Class<? extends IUrlStartPointConverter<T>> urlStartPointConverter;

	private PageWalker<T> pageWalker;

	private Class<? extends ITransitionConverter<T>> transitionConverter;

	private Class<? extends ICustomTestStepConverter<T>> customTestStepConverter;

	
	/**
	 * Public constructor.
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
	 * Traverse the test, using the generic delegates used when constructing instance.
	 * Stops at target page, if non-null.
	 * Converts all paths in test (tree).
	 */
	public void convertTest(Test test, T resultHolder) {
		
		//If multiple paths in tree test, we should start from the very beginning for each path.
		//As we use extension points etc., we do not know in advance how many times to process a node.
		//We therefore use a strategy of looping over the tree many times and maintaing a list of converted nodes.
		//See JUnit test case.
		
		try {
			//be sure to cover all paths:
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
	 * Traverse the test, using the generic delegates used when constructing instance.
	 * Stops at target page and does not convert all paths in tree if target page non-null. 
	 * @param test
	 * @param resultHolder
	 * @param targetPage page that the walker should stop when it gets to
	 */
	public void convertTest(Test test, T resultHolder, Page targetPage) {
		convertTest(test, null, resultHolder, targetPage);
	}
	
	/**
	 * Traverse the test, using the generic delegates used when constructing instance.
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
	 * Converts test node and its children. If targetExtensionPoint is non-null,
	 * only converts test on path to that ExtensionPoint. If node is subtest,
	 * only convert the used path in the subtest (the path leading to the
	 * extension point that is extended from)
	 * 
	 * @param resultHolder
	 * @param node
	 * @param targetExtensionPoint
	 *            if non-null, only convert node on path to this ExtensionPoint.
	 * @param targetPage page that the walker should stop when it gets to
	 * @return whether the node is finished (all paths in tree containing node has been traversed).
	 */
	protected boolean convertTransitionNode(T resultHolder, TransitionNode node, ConnectionPoint targetExtensionPoint, Page targetPage)
			throws InstantiationException, IllegalAccessException {

		//If multiple paths in tree test, we should start from the very beginning for each path.
		//As we use extension points etc., we do not know in advance how many times to process a node.
		//We therefore use a strategy of looping over the tree many times and maintaing a list of converted nodes.
		//See JUnit test case.

		
		boolean nodeFinished = true; //init
		
		if (convertedNodes.contains(node)) {
			return true;
		}

		if (nodeShouldBeConverted(node, targetExtensionPoint, targetPage)) {

			if (node instanceof UrlStartPoint) {
				urlStartPointConverter.newInstance().handleUrlStartPoint(resultHolder, (UrlStartPoint) node, 
						targetExtensionPoint == null);
			} 
			else if (node instanceof ExtensionStartPoint) {
				ExtensionStartPoint exStartPoint = (ExtensionStartPoint) node;
				Test subtestTest = (((SubTest) node).getTest(true));
				ExtensionPoint targetInSubTest = ((ExtensionTransition) exStartPoint.getOutTransitions().get(0)).getExtensionPoint();
				convertTransitionNode(resultHolder, subtestTest.getStartPoint(), targetInSubTest, null);
				resultHolder.updateStatus(((SubTest) node), false, (ExtensionStartPoint) node);
			}
			else if (node instanceof SubTest) {
				SubTest subtest = (SubTest) node;
				Test subtestTest = subtest.getTest(true);
				List<Transition> outTransitions = node.getOutTransitions();
				ExtensionPoint targetExPoint = null;
				if (outTransitions != null && outTransitions.size() > 0 && outTransitions.get(0) instanceof ExtensionTransition) {
						// convert path in SubTest leading to the extension point that is extended from
						targetExPoint = ((ExtensionTransition) outTransitions.get(0)).getExtensionPoint();
				}
				else {
					// other or no transition from SubTest. Check that only one path in it
					if(!ModelUtil.assertHasOnlyOnePathFrom(subtestTest.getStartPoint())) {
						ErrorHandler.logAndShowErrorDialogAndThrow("Error traversing subtest: The \"" + subtest.getFileName() + "\" subtest " +
								"is a tree (has more than one path in it) and an extension point is not used. The exporter does not know which path to execute.\n\n" +
								"To fix, create an extension point in the test, and extend from it where the subtest is used.");
					}
				}
				
				// Set correct parameter in sub test:
				if (subtest.hasOwnParams()) {
					subtestTest.getParamList().setParameterIndex(subtest.getParameterIndex());
					subtestTest.updateObservers();
				}
				
				// Convert SubTest:
				try {
					convertTransitionNode(resultHolder, subtestTest.getStartPoint(), targetExPoint, null);
					resultHolder.updateStatus(subtest, false, targetExtensionPoint);
				}
				catch (Exception e) {
					handleSubTestException(resultHolder, subtest, targetExtensionPoint, e);
				}
			}
			else if (node instanceof Page) {
				pageWalker.handlePage(resultHolder, (Page) node);
			} 
			else if (node instanceof CustomTestStepHolder) {
				CustomTestStepHolder ctsh = (CustomTestStepHolder) node;
				ICustomTestStepConverter<T> ctsc = customTestStepConverter.newInstance();
				ctsc.handleCustomStep(resultHolder,
						ctsh, ctsc.getDataKey() == null ? null : ctsh.getCustomTestStep().getData( ctsc.getDataKey()));
			}

			//OK, page converted, see if we should stop traversing the next nodes:
			if (targetPage != null && node.equals(targetPage)) {
				return true;
			}
			
			int pathNum = 0;
			for (Transition outTransition : node.getOutTransitions()) {
				pathNum++;
				TransitionNode endNode = (TransitionNode) outTransition.getEnd();

				if (convertedNodes.contains(endNode)) {
					continue;
				}
				else {
					if (nodeShouldBeConverted(endNode, targetExtensionPoint, targetPage)) {
						if (outTransition instanceof UserInteractionsTransition) {
							transitionConverter.newInstance().handleUserInteractions(resultHolder,(UserInteractionsTransition) outTransition);
						}
						else {
							//only follow connection, no export
						}
						
						// convert end node recursively: 
						nodeFinished = convertTransitionNode(resultHolder, endNode, targetExtensionPoint, targetPage);

						if (pathNum < node.getOutTransitions().size() && targetExtensionPoint == null) {
							//end node converted, skip to root of tree to start traversal of the other paths from there.
							nodeFinished = false;
							break;
						}
					}
				}
				
			}
			if (targetExtensionPoint != null) {
				//there possibly exists a tree test *after* this test, we do not know whether we are finished
				nodeFinished = false;
			}
			
			if (nodeFinished) {
				convertedNodes.add(node);
			}
			
		}
		else {
			String msg = "Target extension point connected to page not present in test: " + node + ", " + targetExtensionPoint;
			ErrorHandler.logAndShowErrorDialogAndRethrow(msg, new UnknownExtensionPointException(msg));;
		}

		return nodeFinished;
	}



	private boolean nodeShouldBeConverted(TransitionNode node,
			ConnectionPoint targetExtensionPoint, Page targetPage) {
		if (targetExtensionPoint == null && targetPage == null) {
			return true;
		}
		if (targetPage == null) { 
			return targetExtensionPoint == null || TestWalkerUtils.isOnPathToNode(node, targetExtensionPoint);
		}
		if (targetExtensionPoint == null) {
			return targetPage == null || TestWalkerUtils.isOnPathToNode(node, targetPage);
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
