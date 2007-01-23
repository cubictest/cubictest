/*
 * Created on 11.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.PageElement;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.ContextContainerEditPolicy;
import org.cubictest.ui.gef.policies.ContextLayoutEditPolicy;
import org.cubictest.ui.gef.policies.PageElementComponentEditPolicy;
import org.cubictest.ui.gef.policies.PageElementDirectEditPolicy;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


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
			new CubicTestGroupFigure(((AbstractContext)getModel()).getDescription(), false);
		figure.setBackgroundColor(ColorConstants.listBackground);
		figure.setTooltipText("Check context present: " + getElementDescription()
				+ "\nContexts are used for identyfying a part of the page or a single element.");
		return figure;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals(){
		super.refreshVisuals();
		AbstractContext context = (AbstractContext)getModel();
		CubicTestGroupFigure figure = (CubicTestGroupFigure) getFigure();
		figure.setText(context.getDescription());
	}
	
	protected void startDirectEdit(){
		if (manager == null)
			manager = new CubicTestDirectEditManager(this,
					TextCellEditor.class,
					new CubicTestEditorLocator(
							((CubicTestGroupFigure)getFigure()).getHeader()),
					((PageElement)getModel()).getDescription());
		manager.setText(((PageElement)getModel()).getDescription());
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
	
	/*
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		int i = 0;
		List<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();

		TextPropertyDescriptor desc = new TextPropertyDescriptor(this + "-DESCRIPTION", 
				i++ + ": Description:");
		properties.add(desc);

		TextPropertyDescriptor idText = new TextPropertyDescriptor(this + "-IDTEXT", 
				i++ + ": Element ID:");
		properties.add(idText);

		
		for (Object object : getChildren()){
			if (object instanceof IPropertySource){
				if (object instanceof PageElementEditPart){
					PageElementEditPart child = (PageElementEditPart)object;
					properties.add(new TextPropertyDescriptor(child,
							i++ + ": " + child.getType()));
				}
			}
		}
		IPropertyDescriptor[] ipdA = (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});

		return ipdA;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id.toString().endsWith("-DESCRIPTION")) {
			return ((AbstractContext)getModel()).getDescription();
		}
		else if (id.toString().endsWith("-IDTEXT")) {
			return ((AbstractContext)getModel()).getText();
		}
		else {
			for (Object child:getChildren()){
				if (child.equals(id)){
					return child;
				}
			}
		}
		return "getProperty Value " + id.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if (id.toString().endsWith("-DESCRIPTION")) {
			super.setPropertyValue(id, value);
		}
		else if (id.toString().endsWith("-IDTEXT")) {
			AbstractContext modelObj = ((AbstractContext)getModel());
			ChangePageElementTextCommand cmd = new ChangePageElementTextCommand();
			cmd.setPageElement(modelObj);
			cmd.setOldText(modelObj.getText());
			cmd.setNewText((String)value);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
			
		} else {
			for (Object child : getChildren()){
				if (child.equals(id)){
					((IPropertySource)child).setPropertyValue(id,value);
				}
			}
		}
	}
}
