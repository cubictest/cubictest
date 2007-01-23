/*
 * Created on 28.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.resources.UiText;
import org.cubictest.model.SubTest;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.policies.PageNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


/**
 * Controller for the <code>SubTest</code> model.
 * @author ehalvorsen
 */
public class SubTestEditPart extends AbstractNodeEditPart{

	private CubicTestDirectEditManager manager;

	/**
	 * Constructor for the <code>PageEditPart</code> controller.
	 * @param page the model
	 */
	public SubTestEditPart(SubTest page) {
		setModel(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request request) {
		
		if(request.getType() == RequestConstants.REQ_OPEN) {
			String testPath = ((SubTest)getModel()).getFilePath();
			final IFile testFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(testPath));
			
			if (!testFile.exists() || !(testFile instanceof IFile)) {
				MessageDialog.openError(new Shell(), UiText.APP_TITLE, 
						"Test \"" + ((SubTest)getModel()).getFilePath() + "\" does not exist.");
				return;
			}

			(new Shell()).getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, testFile, true);
					} catch (PartInitException e) {
					}
				}
			});
		}
		super.performRequest(request);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		AbstractTransitionNodeFigure figure = new AbstractTransitionNodeFigure();
		figure.setBackgroundColor(ColorConstants.darkBlue);
		figure.setForegroundColor(ColorConstants.white);
		figure.setLocation(((TransitionNode)getModel()).getPosition());
		String name = ((SubTest)getModel()).getName();
		figure.setText(name);
		figure.setToolTipText("Sub test: " + name + "\nDouble click to open file");
		return figure;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new PageNodeEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals(){
		SubTest page = (SubTest)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		String title = ((SubTest)getModel()).getName();
		figure.setText(title);
		Point position = page.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
		if (manager !=null)
			manager.setText(title);
	}
	
	protected List getModelTargetConnections() {
		Transition trans = ((TransitionNode)getModel()).getInTransition();
		List<Transition> list = new ArrayList<Transition>();
		if (trans != null)
			list.add(((TransitionNode)getModel()).getInTransition());
		return list;
	}
	
}
