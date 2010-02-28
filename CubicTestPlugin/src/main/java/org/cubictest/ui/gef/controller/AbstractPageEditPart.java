/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.controller;

import java.util.List;

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.AbstractPageDirectEditPolicy;
import org.cubictest.ui.gef.policies.ContextContainerEditPolicy;
import org.cubictest.ui.gef.policies.ContextLayoutEditPolicy;
import org.cubictest.ui.gef.policies.PageNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.gef.view.CubicTestHeaderLabel;
import org.cubictest.ui.gef.view.CubicTestScrollableGroupFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TextCellEditor;


public abstract class AbstractPageEditPart extends AbstractNodeEditPart {
	
	private CubicTestDirectEditManager manager;


	public Object getEditableValue() {
		return this;
	}

	protected abstract String getType();

	private void startDirectEdit() {
		if (manager == null)
				manager = new CubicTestDirectEditManager(this,
						TextCellEditor.class,
						new CubicTestEditorLocator(((CubicTestGroupFigure)getFigure()).getHeader()),
						((AbstractPage)getModel()).getName());
			manager.setText(((AbstractPage)getModel()).getName());
			manager.show();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	@Override
	public void performRequest(Request request) {
		if(request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN){
			startDirectEdit();
		}
		super.performRequest(request);
	}
	
	@Override
	public IFigure getContentPane() {
		return ((CubicTestScrollableGroupFigure) getFigure()).getContentsPane();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		IFigure figure = new CubicTestScrollableGroupFigure(((AbstractPage)getModel()).getName(), true);
		figure.setLocation(((TransitionNode)getModel()).getPosition());
		return figure;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		ContextLayoutEditPolicy layoutPolicy = new ContextLayoutEditPolicy((IContext)getModel());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, layoutPolicy);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new PageNodeEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContextContainerEditPolicy(layoutPolicy));
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new AbstractPageDirectEditPolicy());
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List<PageElement> getModelChildren(){
		return ((AbstractPage)getModel()).getRootElements();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		if (value == EditPart.SELECTED_NONE) {
			((CubicTestGroupFigure)getFigure()).setSelected(false);
		}
		else {
			((CubicTestGroupFigure)getFigure()).setSelected(true);
		}
		getFigure().repaint();
		
		//If this is the last element created, start direct edit:
		CommandStack stack = getViewer().getEditDomain().getCommandStack();
		if (manager == null && ViewUtil.pageHasJustBeenCreated(stack, (AbstractPage)getModel())) {
			startDirectEdit();			
		}
	}
	
	public boolean isSelected() {
		return getSelected() != SELECTED_NONE;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals(){
		AbstractPage page = (AbstractPage)getModel();
		CubicTestGroupFigure figure = (CubicTestGroupFigure) getFigure();
		
		//Update header dimension:
		Dimension newHeaderDim = new Dimension();
		newHeaderDim.width = figure.getPreferredSize().width;
		newHeaderDim.height = figure.getHeader().getPreferredSize().height;
		figure.getHeader().setPreferredSize(newHeaderDim);
		
		String title = ((AbstractPage)getModel()).getName();
		figure.setText(title);
		Point position = page.getPosition();
		Dimension dimension = page.getDimension();
		Rectangle r = new Rectangle(position.x, position.y, dimension.width, dimension.height);
		((TestEditPart)getParent()).setLayoutConstraint(this, figure, r);
		if (manager != null) {
			manager.setText(title);
		}
	}
	
	public CubicTestHeaderLabel getHeaderFigure() {
		return (CubicTestHeaderLabel) ((CubicTestGroupFigure) getFigure()).getHeader();
	}
}
