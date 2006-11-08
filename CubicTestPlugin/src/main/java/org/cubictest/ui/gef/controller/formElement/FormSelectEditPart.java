/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;
import org.cubictest.ui.gef.command.ChangeMultiSelectCommand;
import org.cubictest.ui.gef.command.ChangePageElementIdentifierTypeCommand;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.ContextLayoutEditPolicy;
import org.cubictest.ui.gef.policies.PageElementComponentEditPolicy;
import org.cubictest.ui.gef.policies.PageElementDirectEditPolicy;
import org.cubictest.ui.gef.policies.SelectContextContainerEditPolicy;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.utils.IdentifierUtil;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


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
			new CubicTestGroupFigure(getModel().getDescription());
		figure.setBackgroundColor(ColorConstants.listBackground);
		figure.getHeader().setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.SELECT_IMAGE));
		return figure;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals(){
		Select select = getModel();
		CubicTestGroupFigure figure = (CubicTestGroupFigure) getFigure();
		figure.setText(select.getDescription());
		if (manager !=null)
			manager.setText(select.getDescription());
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
	
	/*
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		int i = 0;
		List<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		
		TextPropertyDescriptor pd = new TextPropertyDescriptor(this + "-DESCRIPTION", 
				i++ + ": Description:");
		properties.add(pd);

		String[] idTypeValues = IdentifierUtil.toStringArray(getModel().getIdentifierTypes());
		ComboBoxPropertyDescriptor idTypes = 
			new ComboBoxPropertyDescriptor(this + "-IDTYPE", i++ + ": Identifier type", idTypeValues);
		properties.add(idTypes);

		if (!(getModel().getIdentifierType().equals(IdentifierType.LABEL))) {
			TextPropertyDescriptor nameTpd = new TextPropertyDescriptor(this + "-IDTEXT", i++ + ": Text in the identifier");
			properties.add(nameTpd);
		}
		
		String[] cbValues = {"True", "False"};
		ComboBoxPropertyDescriptor cbpd = 
			new ComboBoxPropertyDescriptor(this + "-MULTI",i++ + ": Multiselect", cbValues);
		properties.add(cbpd);

		for (Object object : getChildren()){
			if (object instanceof IPropertySource){
				if (object instanceof PageElementEditPart){
					PageElementEditPart child = (PageElementEditPart)object;
					properties.add(new TextPropertyDescriptor(child,
							i++ + ": " + child.getType()));
				}
			}
		}
		
		return (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id.toString().endsWith("-DESCRIPTION")) {
			return getModel().getDescription();
		}
		else if (id.toString().endsWith("-IDTEXT")) {
			return getModel().getText();
		}
		else if (id.toString().endsWith("-IDTYPE")) {
			return IdentifierUtil.indexOf(getModel().getIdentifierType(), getModel().getIdentifierTypes());
		}
		else if (id.toString().endsWith("-MULTI")){
			if (getModel().isMultiselect()) return new Integer(0);
			else return new Integer(1);
		}
		else {
			for (Object child:getChildren()){
				if (child.equals(id)){
					return child;
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {		
		if (id.toString().endsWith("-DESCRIPTION")) {
			super.setPropertyValue(id, value);
		}
		else if (id.toString().endsWith("-IDTEXT")) {
			PageElement model = ((PageElement)getModel());
			ChangePageElementTextCommand cmd = new ChangePageElementTextCommand();
			cmd.setPageElement(model);
			cmd.setOldText(model.getText());
			cmd.setNewText((String)value);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}
		else if (id.toString().endsWith("-CHECKED")) {
			boolean multi;
			if (((Integer)value).intValue() == 0)
				multi = true;
			else
				multi = false;

			ChangeMultiSelectCommand cmd = new ChangeMultiSelectCommand();
			cmd.setSelect(getModel());
			cmd.setOldMulti(getModel().isMultiselect());
			cmd.setNewMulti(multi);
			getViewer().getEditDomain().getCommandStack().execute(cmd);

		}
		else if (id.toString().endsWith("-IDTYPE")) {
			Select select = getModel();
			ChangePageElementIdentifierTypeCommand cmd = new ChangePageElementIdentifierTypeCommand();
			cmd.setPageElement(select);
			cmd.setOldIdentifierType(select.getIdentifierType());

			int index = ((Integer)value).intValue();
			cmd.setNewIdentifierType(select.getIdentifierTypes().get(index));
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}else {
			for (Object child : getChildren()){
				if (child.equals(id)){
					((IPropertySource)child).setPropertyValue(id,value);
				}
			}
		}
	}
	
}
