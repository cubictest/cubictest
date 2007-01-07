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

import org.cubictest.common.exception.CubicException;
import org.cubictest.common.exception.UnknownExtensionPointException;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserActions;

/**
 * Uses a connection point converter, a page converter and a transition
 * converter to convert a test into e.g. test steps.
 * 
 * @author ovstetun
 * @author SK
 * @author chr_schwarz
 * 
 */
public class TreeTestWalker<T> {

	private List<TransitionNode> convertedNodes = new ArrayList<TransitionNode>();

	private Class<? extends IUrlStartPointConverter<T>> urlStartPointConverter;

	private PageWalker<T> pageWalker;

	private Class<? extends ITransitionConverter<T>> transitionConverter;

	private Class<? extends ICustomTestStepConverter<T>> customTestStepConverter;

	public TreeTestWalker(Class<? extends IUrlStartPointConverter<T>> urlSpc,
			Class<? extends IPageElementConverter<T>> pec,
			Class<? extends IContextConverter<T>> cc,
			Class<? extends ITransitionConverter<T>> tc,
			Class<? extends ICustomTestStepConverter<T>> ctsc) {
		this.urlStartPointConverter = urlSpc;
		this.pageWalker = new PageWalker<T>(pec,cc);
		;
		this.transitionConverter = tc;
		this.customTestStepConverter = ctsc;
	}

	/**
	 * Convert a test to test steps and populate steps into e.g. a step list. If
	 * targetExtensionPoint is non-null, only converts test on path to that
	 * ExtensionPoint.
	 * 
	 * @param test
	 * @param t
	 * @param targetExtensionPoint
	 *            if non-null, only convert test on path to this ExtensionPoint.
	 */
	public void convertTest(Test test, T t, ConnectionPoint targetExtensionPoint) {
		try {
			ConnectionPoint startPoint = test.getStartPoint();
			convertTransitionNode(t, startPoint, targetExtensionPoint);

			for (Transition outTransition : startPoint.getOutTransitions()) {
				TransitionNode node = (TransitionNode) outTransition.getEnd();

				if (convertedNodes.contains(node))
					continue;

				if (startPoint instanceof ExtensionStartPoint) {
					convertTransitionNode(t, node, targetExtensionPoint);
				} else {
					convertTransitionNode(t, node, null);
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new CubicException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new CubicException(e);
		}
	}

	/**
	 * Converts test node and its children. If targetExtensionPoint is non-null,
	 * only converts test on path to that ExtensionPoint. If node is subtest,
	 * only convert the used path in the subtest (the path leading to the
	 * extension point that is extended from)
	 * 
	 * @param t
	 * @param node
	 * @param targetExtensionPoint
	 *            if non-null, only convert node on path to this ExtensionPoint.
	 */
	private void convertTransitionNode(T t, TransitionNode node,
			ConnectionPoint targetExtensionPoint)
			throws InstantiationException, IllegalAccessException {

		if (targetExtensionPoint == null
				|| isOnExtensionPointPath(node, targetExtensionPoint)) {

			if (node instanceof UrlStartPoint) {
				urlStartPointConverter.newInstance().handleUrlStartPoint(t,
						(UrlStartPoint) node);
			} else if (node instanceof ExtensionStartPoint) {
				convertTest(((SubTest) node).getTest(), t,
						(ExtensionStartPoint) node);
			} else if (node instanceof SubTest) {
				Test test = ((SubTest) node).getTest();
				List<Transition> out = node.getOutTransitions();
				if (out.size() > 1) {
					throw new CubicException(
							"More than one out transition from subtest is not allowed.");
				} else if (out.size() == 1) {
					// only convert path in subtest leading to the extension
					// point that is extended from
					convertTest(test, t, ((ExtensionTransition) out.get(0))
							.getExtensionPoint());
				} else {
					convertTest(test, t, null);
				}
			} else if (node instanceof Page) {
				pageWalker.handlePage(t, (Page) node);
			} else if (node instanceof CustomTestStep) {
				customTestStepConverter.newInstance().handleCustomStep(t,
						(CustomTestStep) node);
			} else {
				return;
			}

			for (Transition outTransition : node.getOutTransitions()) {
				TransitionNode endNode = (TransitionNode) outTransition
						.getEnd();

				if (convertedNodes.contains(endNode))
					continue;

				if (targetExtensionPoint == null
						|| isOnExtensionPointPath(endNode, targetExtensionPoint)) {
					if (outTransition instanceof UserActions) {
						transitionConverter.newInstance().handleUserInteractions(t,(UserActions) outTransition);
					}
					else {
						//only follow transition, no export
					}
					// recursive:
					convertTransitionNode(t, endNode, targetExtensionPoint);
				}
			}

			convertedNodes.add(node);
		}

		else if (targetExtensionPoint != null
				&& (node instanceof UrlStartPoint || node instanceof ExtensionStartPoint)) {
			// start point was not on path to extension point. Not possible to
			// continue.
			// show decent error message:
			String pageId = "unknownId";
			if (targetExtensionPoint instanceof ExtensionStartPoint)
				pageId = ((ExtensionStartPoint) targetExtensionPoint)
						.getSourceExtensionPointPageId();
			else if (targetExtensionPoint instanceof ExtensionPoint)
				pageId = ((ExtensionPoint) targetExtensionPoint).getPageId();
			throw new UnknownExtensionPointException(
					"Target extension point connected to page with ID "
							+ pageId + " not present in test.");
		}
	}

	/**
	 * Checks whether a node is on the path to the targeted extension point.
	 */
	public boolean isOnExtensionPointPath(TransitionNode node,
			ConnectionPoint targetExtensionPoint)
			throws InstantiationException, IllegalAccessException {

		String targetPageId = "";
		if (targetExtensionPoint instanceof ExtensionStartPoint) {
			targetPageId = ((ExtensionStartPoint) targetExtensionPoint)
					.getSourceExtensionPointPageId();
		} else if (targetExtensionPoint instanceof ExtensionPoint) {
			targetPageId = ((ExtensionPoint) targetExtensionPoint).getPageId();
		} else {
			throw new CubicException("Unsupported connection point: "
					+ targetExtensionPoint.toString());
		}

		if (node instanceof Page) {
			if (node.getId().equals(targetPageId)) {
				return true;
			}
		}

		for (Transition outTransition : node.getOutTransitions()) {
			TransitionNode endNode = (TransitionNode) outTransition.getEnd();
			if (isOnExtensionPointPath(endNode, targetExtensionPoint)) {
				return true;
			}
		}

		return false;
	}
}
