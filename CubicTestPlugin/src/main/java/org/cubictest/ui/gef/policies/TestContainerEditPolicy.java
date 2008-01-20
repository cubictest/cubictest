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
package org.cubictest.ui.gef.policies;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.AddCustomTestStepCommand;
import org.cubictest.ui.gef.command.AddSubTestCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;


/**
 * @author SK Skytteren
 * 
 */
public class TestContainerEditPolicy extends ContainerEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object newObject = request.getNewObject();
		Point location = request.getLocation();
		TestEditPart editPart = (TestEditPart) getHost();
		Test test = (Test) editPart.getModel();

		if (newObject instanceof AbstractPage) {
			AbstractPage page = (AbstractPage) newObject;
			page.setPosition(location);
			if (page instanceof Common) {
				page.setName("Common");
			}
			AddAbstractPageCommand pageAddCommand = new AddAbstractPageCommand();
			pageAddCommand.setTest(test);
			pageAddCommand.setPage(page);
	
			return pageAddCommand;
		}
		else if(newObject instanceof SubTest) {
			SubTest subTest = (SubTest) newObject;
			if (test.getFilePath().equals(subTest.getTest(false).getFilePath())) {
				//nesting of subtests not allowed
				return null;
			}
			subTest.setPosition(location);
			AddSubTestCommand addSubTestCmd = new AddSubTestCommand(subTest, test);
			if (test.getStartPoint() instanceof TestSuiteStartPoint) {
				CreateTransitionCommand createTransitionCmd = new CreateTransitionCommand();
				createTransitionCmd.setSource(getLastNodeInGraph(test.getStartPoint()));
				createTransitionCmd.setTarget(subTest);
				createTransitionCmd.setTest(test);
				return addSubTestCmd.chain(createTransitionCmd);
			}
			else {
				return addSubTestCmd;
			}
		}
		else if(newObject instanceof CustomTestStepHolder) {
			CustomTestStepHolder testStep = (CustomTestStepHolder) newObject;
			testStep.setPosition(location);
			AddCustomTestStepCommand addCustomTestStepCommand = new AddCustomTestStepCommand((Test) getHost().getModel(), testStep);
			return addCustomTestStepCommand;
		}
		return null;
	}

	private TransitionNode getLastNodeInGraph(TransitionNode node) {
		if (node.getOutTransitionsWithoutExtensionPoints().size() == 0) {
			return node;
		}
		else {
			return getLastNodeInGraph(node.getOutTransitionsWithoutExtensionPoints().get(0).getEnd());
		}
	}

	@Override
	protected Command getOrphanChildrenCommand(GroupRequest request) {
		return super.getOrphanChildrenCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		if (REQ_CREATE.equals(request.getType()))
			return getHost();
		return super.getTargetEditPart(request);
	}
}
