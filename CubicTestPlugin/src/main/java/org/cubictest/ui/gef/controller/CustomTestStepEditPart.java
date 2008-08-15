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

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.resources.UiText;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.CustomTestStepEvent;
import org.cubictest.model.customstep.ICustomStepListener;
import org.cubictest.ui.customstep.CustomStepEditor;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.cubictest.ui.gef.view.CustomTestStepFigure;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
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
 * @author SK Skytteren
 * Contoller for the <code>ExtensionPoint</code> model.
 *
 */
public class CustomTestStepEditPart extends AbstractNodeEditPart implements ICustomStepListener {

	private CustomTestStepFigure customTestStepFigure;

	/**
	 * Constructor for <code>ExtensionPointEditPart</code>.
	 * @param step the model
	 */
	public CustomTestStepEditPart(CustomTestStepHolder step) {
		setModel(step);
	}

	
	@Override
	public void activate() {
		super.activate();
		((CustomTestStepHolder)getModel()).getCustomTestStep(false).addCustomStepListener(this);
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		((CustomTestStepHolder)getModel()).getCustomTestStep(false).removeCustomStepListener(this);
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		String name = ((CustomTestStepHolder)getModel()).getName();
		String filePath = ((CustomTestStepHolder)getModel()).getFilePath();
		customTestStepFigure = new CustomTestStepFigure(name);
		Point p = ((TransitionNode)getModel()).getPosition();
		customTestStepFigure.setLocation(p);
		customTestStepFigure.setText(name);
		customTestStepFigure.setToolTipText(getTooltipText());
		return customTestStepFigure;
	}

	
	private String getTooltipText() {
		String filePath = ((CustomTestStepHolder)getModel()).getFilePath();
		String description = ((CustomTestStepHolder)getModel()).getDescription();
		return "Custom test step: $labelText\nFile: " + filePath + 
				((StringUtils.isBlank(description)) ? "" : ("\n\n" + description));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		ConnectionPoint connectionPoint = (ConnectionPoint)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		customTestStepFigure.setText(((CustomTestStepHolder)getModel()).getName());
		customTestStepFigure.setToolTipText(getTooltipText());
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	@Override
	public void performRequest(Request request) {
		
		if(request.getType() == RequestConstants.REQ_OPEN){
			final IFile file = ((CustomTestStepHolder)getModel()).getFile();
			if (!file.exists() || !(file instanceof IFile)) {
				MessageDialog.openError(new Shell(), UiText.APP_TITLE, 
						"Container \"" + ((CustomTestStepHolder)getModel()).getFilePath() + "\" does not exist in this project.");
				return;
			}
			
			//refresh before open:
			((CustomTestStepHolder)getModel()).getCustomTestStep(false).removeCustomStepListener(this);
			final CustomTestStep refreshed = ((CustomTestStepHolder)getModel()).getCustomTestStep(true);
			refreshed.addCustomStepListener(this);

			(new Shell()).getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IEditorPart part = IDE.openEditor(page, file, true);
						if(part instanceof CustomStepEditor){
							((CustomStepEditor)part).setCustomStep(refreshed);
						}
					} catch (PartInitException e) {
						Logger.warn("Failed to open custom step", e);
					}
				}
			});
		}
		super.performRequest(request);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(PropertyAwareObject.STATUS.equals(evt.getPropertyName())){
			if (evt.getNewValue() instanceof TestPartStatus) {
				customTestStepFigure.setStatus((TestPartStatus) evt.getNewValue());
			}
		}
		else
			super.propertyChange(evt);
	}


	public void handleEvent(CustomTestStepEvent event) {
		String key = event.getKey();
		if (CustomTestStep.NAME_CHANGED.equals(key)) {
			refreshVisuals();
		}
	}
	
}