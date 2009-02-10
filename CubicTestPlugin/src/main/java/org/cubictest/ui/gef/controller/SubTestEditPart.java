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
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.resources.UiText;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.policies.PageNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.SubTestFigure;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
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
	private boolean initRefreshed = false;

	/**
	 * Constructor for the <code>PageEditPart</code> controller.
	 * @param page the model
	 */
	public SubTestEditPart(SubTest page) {
		setModel(page);
	}

	
	@Override
	public void activate() {
		if (!initRefreshed) {
			//must have an additional refresh as start point could change as page opened (UpdateExtensionStartPointWizard).
			refresh();
			initRefreshed = true;
		}
		super.activate();
		ViewUtil.getSurroundingTest(this).addPropertyChangeListener(this);
		getModel().getTest(false).addPropertyChangeListener(this);
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		ViewUtil.getSurroundingTest(this).removePropertyChangeListener(this);
		getModel().getTest(false).removePropertyChangeListener(this);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt){
		String property = evt.getPropertyName();
		if (evt.getSource() instanceof Test && evt.getPropertyName().equals(PropertyAwareObject.NAME)) {
			//refresh name
			refresh();
		}
		else if (PropertyAwareObject.STATUS.equals(property)) {
			TestPartStatus newStatus = (TestPartStatus)evt.getNewValue();
			((SubTestFigure)getFigure()).setStatus(newStatus);
		}
		else if (evt.getOldValue() instanceof SubTest && ((SubTest)evt.getOldValue()).isEqualTo(this.getModel()) && evt.getNewValue() == null) {
			//this subtest is removed. Do not refresh
		}
		else {
			//source is self, refresh on all changes
			refresh();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	@Override
	public void performRequest(Request request) {
		
		if(request.getType() == RequestConstants.REQ_OPEN) {
			String testPath = ((SubTest)getModel()).getFilePath();
			IProject project = ((SubTest)getModel()).getProject();
			final IFile testFile = project.getFile(new Path(testPath));
			
			if (!testFile.exists() || !(testFile instanceof IFile)) {
				MessageDialog.openError(new Shell(), UiText.APP_TITLE, 
						getFileNotFoundMessage());
				return;
			}

			(new Shell()).getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IEditorPart part = IDE.openEditor(page, testFile, true);
						if(part instanceof GraphicalTestEditor){
							GraphicalViewer viewer = ((GraphicalTestEditor)part).getGraphicalViewer();
							viewer.setContents(getModel().getTest(false));
							viewer.select(viewer.getContents());
						}
					} catch (PartInitException e) {
						Logger.error(e);
					}
				}
			});
		}
		super.performRequest(request);
	}


	protected String getFileNotFoundMessage() {
		return "Error opening subtest:\nFile \"" + ((SubTest)getModel()).getFilePath() + "\" not found.\n" +
				"To fix, delete sub test node and add it again.";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		String name = getModel().getName();
		SubTestFigure figure = new SubTestFigure(name);
		figure.setLocation(((TransitionNode)getModel()).getPosition());
		figure.setToolTipText("Sub test: $labelText\nDouble click to open file");
		return figure;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new PageNodeEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals(){
		SubTest subtest = getModel();
		SubTestFigure figure = (SubTestFigure) getFigure();
		String title = getModel().getName();
		figure.setText(title);
		Point position = subtest.getPosition();
		Dimension dim = figure.getMinimumSize();
		Rectangle r = new Rectangle(position.x, position.y, dim.width, dim.height);
		((TestEditPart)getParent()).setLayoutConstraint(this, figure, r);
		if (manager !=null) {
			manager.setText(title);
		}
		figure.setStatus(subtest.getStatus());
	}
	
	@Override
	protected List getModelTargetConnections() {
		Transition trans = getModel().getInTransition();
		List<Transition> list = new ArrayList<Transition>();
		if (trans != null)
			list.add(getModel().getInTransition());
		return list;
	}
	
	
	@Override
	public SubTest getModel() {
		return (SubTest) super.getModel();
	}
}
