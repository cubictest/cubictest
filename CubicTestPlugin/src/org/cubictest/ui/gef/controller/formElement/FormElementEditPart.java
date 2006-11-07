/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import java.util.ArrayList;

import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.formElement.Checkable;
import org.cubictest.ui.gef.command.ChangeFormElementCheckedCommand;
import org.cubictest.ui.gef.command.ChangePageElementIdentifierTypeCommand;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.cubictest.ui.utils.IdentifierUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * Abstract <code>EditPart</code>that provides functionality for the the other form edit parts.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public abstract class FormElementEditPart extends PageElementEditPart implements IPropertySource{

	CubicTestDirectEditManager manager;

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure(){
		TestStepLabel label = new TestStepLabel(((FormElement)getModel()).getDescription());
		label.setLabelAlignment(PositionConstants.LEFT);
		return label;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.cubictest.ui.gef.controller.PageElementEditPart#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors(){
		int i = 0;
		
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();

		TextPropertyDescriptor pd = new TextPropertyDescriptor(this + "-DESCRIPTION", 
				i++ + ": Description:");
		properties.add(pd);

		String[] idTypeValues = IdentifierUtil.toStringArray(((FormElement)getModel()).getIdentifierTypes());
		ComboBoxPropertyDescriptor idTypes = 
			new ComboBoxPropertyDescriptor(this + "-IDTYPE", i++ + ": Identifier type", idTypeValues);
		properties.add(idTypes);

		if (!((FormElement) getModel()).getIdentifierType().equals(IdentifierType.LABEL)) {
			TextPropertyDescriptor nameTpd = new TextPropertyDescriptor(this + "-IDTEXT", i++ + ": Text in the identifier");
			properties.add(nameTpd);
		}

		if (getModel() instanceof Checkable) {
			String[] cbValues = {"True", "False"};
			ComboBoxPropertyDescriptor cbpd = 
				new ComboBoxPropertyDescriptor(this + "-CHECKED",i++ + ": Checked", cbValues);
			properties.add(cbpd);
		}

		return (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id.toString().endsWith("-DESCRIPTION")) {
			return ((FormElement)getModel()).getDescription();
		}
		else if (id.toString().endsWith("-IDTEXT")) {
			return ((FormElement)getModel()).getText();
		}
		else if (id.toString().endsWith("-IDTYPE")) {
			FormElement model = (FormElement) getModel();
			return IdentifierUtil.indexOf(model.getIdentifierType(), model.getIdentifierTypes());
		}
		else if (id.toString().endsWith("-CHECKED")){
			if (((Checkable)getModel()).isChecked()) return new Integer(0);
			else return new Integer(1);
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
			boolean checked;
			if (((Integer)value).intValue() == 0)
				checked = true;
			else
				checked = false;

			Checkable modelObj = ((Checkable)getModel());
			ChangeFormElementCheckedCommand cmd = new ChangeFormElementCheckedCommand();
			cmd.setCheckable(modelObj);
			cmd.setOldChecked(modelObj.isChecked());
			cmd.setNewChecked(checked);
			getViewer().getEditDomain().getCommandStack().execute(cmd);

		}
		else if (id.toString().endsWith("-IDTYPE")) {
			FormElement modelObj = ((FormElement)getModel());
			ChangePageElementIdentifierTypeCommand cmd = new ChangePageElementIdentifierTypeCommand();
			cmd.setPageElement(modelObj);
			cmd.setOldIdentifierType(modelObj.getIdentifierType());

			int index = ((Integer)value).intValue();
			cmd.setNewIdentifierType(modelObj.getIdentifierTypes().get(index));
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		FormElement element = (FormElement)getModel();
		CubicTestLabel figure = (CubicTestLabel) getFigure();
		figure.setText(element.getDescription());
		if (manager !=null)
			manager.setText(element.getDescription());
	}
	

}

