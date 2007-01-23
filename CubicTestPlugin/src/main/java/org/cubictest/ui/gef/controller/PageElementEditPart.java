/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import static org.cubictest.model.SationObserver.SationType.BOTH;
import static org.cubictest.model.SationObserver.SationType.INTERNATIONALISATION;
import static org.cubictest.model.SationObserver.SationType.NONE;
import static org.cubictest.model.SationObserver.SationType.PARAMETERISATION;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.SationObserver;
import org.cubictest.model.Test;
import org.cubictest.model.SationObserver.SationType;
import org.cubictest.model.parameterization.Parameter;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.ui.gef.command.ChangePageElementDescriptionCommand;
import org.cubictest.ui.gef.command.ChangePageElementIdentifierTypeCommand;
import org.cubictest.ui.gef.command.ChangePageElementNotCommand;
import org.cubictest.ui.gef.command.ChangePageElementSationKeyCommand;
import org.cubictest.ui.gef.command.ChangePageElementSationTypeCommand;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.PageElementComponentEditPolicy;
import org.cubictest.ui.gef.policies.PageElementDirectEditPolicy;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.cubictest.ui.utils.IdentifierUtil;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


public abstract class PageElementEditPart extends PropertyChangePart 
		implements IPropertySource{
	
	protected CubicTestDirectEditManager manager;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageElementComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new PageElementDirectEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request request) {
		
		if(request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN){
			startDirectEdit();
		}
		super.performRequest(request);
	}
	
	protected void startDirectEdit(){
		if (manager == null)
			manager = new CubicTestDirectEditManager(this,
					TextCellEditor.class,
					new CubicTestEditorLocator(
							((CubicTestLabel)getFigure())),
					((PageElement)getModel()).getDescription());
		manager.setText(((PageElement)getModel()).getDescription());
		manager.show();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
	public void setSelected(int value) {
		super.setSelected(value);
		if (getFigure() instanceof CubicTestLabel){
			CubicTestLabel figure = (CubicTestLabel) getFigure();
			if (value != EditPart.SELECTED_NONE)
				figure.setSelected(true);
			else
				figure.setSelected(false);
			figure.repaint();
		}
		
		// If this is the last element created, start direct edit
		CommandStack stack = getViewer().getEditDomain().getCommandStack();
		if (manager == null && ViewUtil.pageElementHasJustBeenCreated(stack, (PageElement)getModel()))
			startDirectEdit();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if(value == null)
			return;
		PageElement pe = (PageElement)getModel();
		if (id.toString().endsWith("-NOT")){
			boolean not = false;
			if (((Integer)value).intValue() == 0) {
				not = true;
			}
			ChangePageElementNotCommand cmd = new ChangePageElementNotCommand();
			cmd.setPageElement(pe);
			cmd.setOldNot(pe.isNot());
			cmd.setNewNot(not);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}
		else if (id.toString().endsWith("-SATION")){
			removeI18nObservers();
			removeParamObservers();
			
			ChangePageElementSationTypeCommand cmd = new ChangePageElementSationTypeCommand();
			cmd.setPageElement(pe);
			cmd.setOldSationType(pe.getSationType());
			
			if (((Integer)value).intValue() == 0){
				cmd.setNewSationType(NONE);
			}
			else if (((Integer)value).intValue() == 1 && getTest().getAllLanuages() != null){
				if(!INTERNATIONALISATION.equals(pe.getSationType())) {
					getTest().getAllLanuages().addObserver(pe);
				}
				cmd.setNewSationType(INTERNATIONALISATION);
			}
			else if (((Integer)value).intValue() == 3){
				int index = getTest().getParamList().getParameters().indexOf(pe.getKey());
				
				if(BOTH.equals(pe.getSationType()))
					return;
				if(INTERNATIONALISATION.equals(pe.getSationType()))
					getTest().getParamList().getParameters().get(index < 0 ? 0 : index).addObserver(pe);
				if(PARAMETERISATION.equals(pe.getSationType()))
					getTest().getAllLanuages().addObserver(pe);
				else {
					getTest().getParamList().getParameters().get((index < 0) ? 0 : index).addObserver(pe);
					getTest().getAllLanuages().addObserver(pe);
				}
				cmd.setNewSationType(BOTH);
				getTest().updateObservers();
			}
			else {
				if(PARAMETERISATION.equals(pe.getSationType()))
					return;
				cmd.setNewSationType(PARAMETERISATION);
				int index = getTest().getParamList().getParameters().indexOf(pe.getKey());
				getTest().getParamList().getParameters().get((index < 0) ? 0 : index).addObserver(pe);
				getTest().updateObservers();
			}
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}
		else if (id.toString().endsWith("-SATIONKEY")){
			int i = (Integer)value;
			SationType type = ((SationObserver)getModel()).getSationType();
			String[] keys;
			if( type.equals(INTERNATIONALISATION)){
				keys = getTest().getAllLanuages().getAllKeys().toArray(new String[]{});
			}else{ 
				ParameterList paramList = getTest().getParamList();
				keys = paramList.getHeaders().toArray();
				removeParamObservers();
				getTest().getParamList().getParameters().get(i).addObserver(pe);
			}
			ChangePageElementSationKeyCommand cmd = new ChangePageElementSationKeyCommand();
			cmd.setPageElement(pe);
			cmd.setOldKey(pe.getKey());
			cmd.setNewKey(keys[i]);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
			getTest().updateObservers();
		}
		else if (id.toString().endsWith("-IDTYPE")) {
			PageElement model = ((PageElement)getModel());
			ChangePageElementIdentifierTypeCommand cmd = new ChangePageElementIdentifierTypeCommand();
			cmd.setPageElement(model);
			cmd.setOldIdentifierType(model.getIdentifierType());

			int index = ((Integer)value).intValue();
			cmd.setNewIdentifierType(model.getIdentifierTypes().get(index));
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}			
		else if (id.toString().endsWith("-TEXT")) {
			PageElement model = ((PageElement)getModel());
			ChangePageElementTextCommand cmd = new ChangePageElementTextCommand();
			cmd.setPageElement(model);
			cmd.setOldText(model.getText());
			cmd.setNewText((String)value);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}else{
			//change description:
			
			PageElement model = (PageElement)getModel();
			ChangePageElementDescriptionCommand cmd = new ChangePageElementDescriptionCommand();
			cmd.setPageElement(model);
			cmd.setOldDescription(model.getDescription());
			cmd.setNewDescription((String) value);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
			
			pe.setSationType(NONE);
		}
	}

	private void removeI18nObservers(){
		PageElement pe = (PageElement) getModel();
		if(getTest().getAllLanuages() != null){
			getTest().getAllLanuages().removeObserver(pe);
		}

	}
	private void removeParamObservers(){
		PageElement pe = (PageElement) getModel();
		if(getTest().getParamList() != null){
			for(Parameter parameter : getTest().getParamList().getParameters()){
				parameter.removeObserver(pe);
			}
		}
	}
	/**
	 * As the <code>getPropertyDescriptors()</code> method but uses the <code>i</code>
	 * for setting the catergory.
	 * @param i
	 * @return
	 */
	public IPropertyDescriptor[] getPropertyDescriptors(){
		int i = 0;
		
		ArrayList<IPropertyDescriptor> properties = 
			new ArrayList<IPropertyDescriptor>();

		TextPropertyDescriptor tpd = new TextPropertyDescriptor(this,i++ + ": Text");
		properties.add(tpd);

		String[] idTypeValues = IdentifierUtil.toStringArray(((PageElement)getModel()).getIdentifierTypes());
		if (idTypeValues.length > 1) {
			ComboBoxPropertyDescriptor idTypes = 
				new ComboBoxPropertyDescriptor(this + "-IDTYPE", i++ + ": Identifier type", idTypeValues);
			properties.add(idTypes);
		}

		if (!((PageElement) getModel()).getIdentifierType().equals(IdentifierType.LABEL)) {
			TextPropertyDescriptor nameTpd = new TextPropertyDescriptor(this + "-TEXT", i++ + ": Text in the identifier");
			properties.add(nameTpd);
		}

		String[] cbValues = {"Not", ""};
		ComboBoxPropertyDescriptor cbpd = 
			new ComboBoxPropertyDescriptor(this + "-NOT",i++ + ": Present", cbValues);
		properties.add(cbpd);
		
		List<String> sationTypes = new ArrayList<String>();
		sationTypes.add("None");
		boolean i18n = false;
		if(getTest()!=null){
			if(getTest().getAllLanuages()!=null){
				sationTypes.add("Internationalisation");
				i18n = true;
			}
			if(getTest().getParamList()!=null){
				sationTypes.add("Parameterisation");
				if(i18n)
					sationTypes.add("Both");
			}
		}
		cbpd = new ComboBoxPropertyDescriptor(this + "-SATION",i++ + ": I18n and Parameters", 
				sationTypes.toArray(new String[0]));
		properties.add(cbpd);
		
		if(!((SationObserver)getModel()).getSationType().equals(NONE)){
			String[] alternatives;
			String text;
			if(((SationObserver)getModel()).getSationType().equals(INTERNATIONALISATION)){
				text = i++ + ": I18n keyword";
				alternatives = getTest().getAllLanuages().getAllKeys().toArray(new String[]{});
			}else if(((SationObserver)getModel()).getSationType().equals(PARAMETERISATION)){
				text = i++ + ": Parameters keyword";
				alternatives = getTest().getParamList().getHeaders().toArray();
			}else {
				text = i++ + ": I18n and Parameters keyword";
				alternatives = getTest().getParamList().getHeaders().toArray();
			}
			cbpd = new ComboBoxPropertyDescriptor(this + "-SATIONKEY",text,alternatives);
			properties.add(cbpd);
		}
		return (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		if (manager != null)
			manager.setText(((PageElement)getModel()).getDescription());
	}
	
	private Test getTest(){
		EditPart ep = getParent();
		while(!(ep instanceof TestEditPart)){
			if(ep == null)
				return null;
			ep = ep.getParent();
		}
		return (Test)((TestEditPart)ep).getModel();
	}

	public final String getType(){
		return ((PageElement)getModel()).getType();
	}
	

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id.toString().endsWith("-NOT")){
			if (((PageElement)getModel()).isNot()) return new Integer(0);
			else return new Integer(1);
		}
		if (id.toString().endsWith("-SATION")){
			SationType type = ((SationObserver)getModel()).getSationType();
			if (type == NONE)
				return 0;
			else if (type == BOTH)
				return 3;
			else if (type == INTERNATIONALISATION)
				return 1;
			else if (type == PARAMETERISATION && 
					getTest().getAllLanuages() != null)
				return 2;
			else 
				return 1;
		}
		if (id.toString().endsWith("-IDTYPE")) {
			IdentifierType idType = ((PageElement)getModel()).getIdentifierType();
			return IdentifierUtil.indexOf(idType, ((PageElement)getModel()).getIdentifierTypes());
		}
		else if (id.toString().endsWith("-TEXT")) {
			return ((PageElement)getModel()).getText();
		}
		if(id.toString().endsWith("-SATIONKEY")){
			SationType type = ((SationObserver)getModel()).getSationType();
			String key = ((PageElement)getModel()).getKey();
			String[] keys;
			if( type.equals(INTERNATIONALISATION)){
				keys = getTest().getAllLanuages().getAllKeys().toArray(new String[]{});
			}else{
				keys = getTest().getParamList().getHeaders().toArray();
			}
			for(int i = 0; i < keys.length; i++){
				if (keys[i].equals(key))
					return i;
			}				
			return 0;
		}
		return ((PageElement)getModel()).getDescription();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return null;
	}
	
	public String getElementDescription() {
		PageElement e = ((PageElement) getModel());
		return StringUtils.isBlank(e.getDescription()) ? e.getText() : e.getDescription();
	}

}
