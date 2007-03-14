/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import java.util.List;

import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.ContextLayoutEditPolicy;
import org.cubictest.ui.gef.policies.PageElementComponentEditPolicy;
import org.cubictest.ui.gef.policies.PageElementDirectEditPolicy;
import org.cubictest.ui.gef.policies.SelectContextContainerEditPolicy;
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
 * @author SK Skytteren
 *
 * Controller for the <code>Select</code> class. 
 */
public class FormSelectEditPart extends FormElementEditPart {

	/**
	 * Constructor for the <code>FormSelectEditPart</code>which is the 
	 * controller for the <code>Select</code> class. 
	 * @param select
	 */
	public FormSelectEditPart(Select select) {
		setModel(select);
	}

	@Override
	public Select getModel() {
		return (Select) super.getModel();
	}
	
	@Override
	public List getModelChildren() {
		return getModel().getElements();
	}

	@Override
	protected CubicTestGroupFigure createFigure() {
		CubicTestGroupFigure figure = 
			new CubicTestGroupFigure(getModel().getText(), false);
		figure.setBackgroundColor(ColorConstants.listBackground);
		figure.getHeader().setIcon(getImage(getModel().isNot()));
		figure.setTooltipText("Check select box present: $labelText");
		return figure;
	}
	
	protected void startDirectEdit(){
		if (manager == null)
			manager = new CubicTestDirectEditManager(this,
					TextCellEditor.class,
					new CubicTestEditorLocator(
							((CubicTestGroupFigure)getFigure()).getHeader()),
					getModel().getDescription());
		manager.setText(getModel().getDescription());
		manager.show();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		ContextLayoutEditPolicy layoutPolicy = new ContextLayoutEditPolicy((IContext)getModel());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, layoutPolicy);
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new SelectContextContainerEditPolicy(layoutPolicy));
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageElementComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new PageElementDirectEditPolicy());
	}

	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(CubicTestImageRegistry.SELECT_IMAGE,isNot);
	}
	
}
