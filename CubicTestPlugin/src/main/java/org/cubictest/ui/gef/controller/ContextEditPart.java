/*
 * Created on 11.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import java.util.List;

import org.cubictest.model.PageElement;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.ContextContainerEditPolicy;
import org.cubictest.ui.gef.policies.ContextLayoutEditPolicy;
import org.cubictest.ui.gef.policies.PageElementComponentEditPolicy;
import org.cubictest.ui.gef.policies.PageElementDirectEditPolicy;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;


/**
 * Edit part for AbstractContext model objects.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public class ContextEditPart extends PageElementEditPart {

	public ContextEditPart(AbstractContext context) {
		setModel(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		ContextLayoutEditPolicy layoutPolicy = new ContextLayoutEditPolicy((IContext)getModel());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, layoutPolicy);
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContextContainerEditPolicy(layoutPolicy));
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageElementComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new PageElementDirectEditPolicy());
	}
	
	@Override
	public AbstractContext getModel() {
		return (AbstractContext) super.getModel();
	}
	
	@Override
	public List getModelChildren() {
		return ((AbstractContext)getModel()).getElements();
	}

	@Override
	protected CubicTestGroupFigure createFigure() {
		CubicTestGroupFigure figure = 
			new CubicTestGroupFigure(((AbstractContext)getModel()).getText(), false);
		figure.setBackgroundColor(ColorConstants.listBackground);
		figure.getHeader().setIcon(getImage(getModel().isNot()));
		figure.setTooltipText("Check context present: $labelText"
				+ "\nContexts are used for identyfying a part of the page or a single page element.");
		return figure;
	}
	@Override
	public void startDirectEdit(){
		if (manager == null)
			manager = new CubicTestDirectEditManager(this,
					TextCellEditor.class,
					new CubicTestEditorLocator(
							((CubicTestGroupFigure)getFigure()).getHeader()),
					((PageElement)getModel()).getText());
		manager.setText(((PageElement)getModel()).getText());
		manager.show();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		CubicTestGroupFigure figure = (CubicTestGroupFigure) getFigure();
		if (value != EditPart.SELECTED_NONE)
			figure.setSelected(true);
		else
			figure.setSelected(false);
		figure.repaint();
		CommandStack stack = getViewer().getEditDomain().getCommandStack();
		if (manager == null && ViewUtil.pageElementHasJustBeenCreated(stack, getModel()))
			startDirectEdit();
	}

	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(CubicTestImageRegistry.CONTEXT_IMAGE, isNot);
	}
}
