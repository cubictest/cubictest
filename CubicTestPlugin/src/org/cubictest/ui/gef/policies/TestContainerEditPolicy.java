/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.AddCustomTestStepCommand;
import org.cubictest.ui.gef.command.AddSubTestCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;


/**
 * @author Stein Kåre Skytteren
 * 
 */
public class TestContainerEditPolicy extends ContainerEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		Object newObject = request.getNewObject();
		Point location = request.getLocation();

		if (newObject instanceof AbstractPage) {
			TestEditPart editPart = (TestEditPart) getHost();
			Test test = (Test) editPart.getModel();
			AbstractPage page = (AbstractPage) newObject;
			page.setPosition(location);
			AddAbstractPageCommand pageAddCommand = new AddAbstractPageCommand();
			pageAddCommand.setTest(test);
			pageAddCommand.setPage(page);
	
			return pageAddCommand;
		}else if(newObject instanceof SubTest) {
			SubTest subTest = (SubTest) newObject;			
			TestEditPart editPart = (TestEditPart) getHost();
			Test test = (Test) editPart.getModel();
			subTest.setPosition(location);
			AddSubTestCommand subTestCommand = new AddSubTestCommand();
			subTestCommand.setTest(test);
			subTestCommand.setSubTest(subTest);
			return subTestCommand;
		} else if(newObject instanceof CustomTestStep) {
			CustomTestStep testStep = (CustomTestStep) newObject;
			testStep.setPosition(location);
			AddCustomTestStepCommand addCustomTestStepCommand = new AddCustomTestStepCommand((Test) getHost().getModel(), testStep);
			return addCustomTestStepCommand;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getOrphanChildrenCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command getOrphanChildrenCommand(GroupRequest request) {
		return super.getOrphanChildrenCommand(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	public EditPart getTargetEditPart(Request request) {
		if (REQ_CREATE.equals(request.getType()))
			return getHost();
		return super.getTargetEditPart(request);
	}
}
