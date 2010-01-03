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
package org.cubictest.ui.customstep;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.CustomTestStepEvent;
import org.cubictest.model.customstep.ICustomStepListener;
import org.cubictest.persistence.CustomTestStepPersistance;
import org.cubictest.ui.customstep.command.ChangeCustomStepDescriptionCommand;
import org.cubictest.ui.customstep.command.ChangeCustomStepNameCommand;
import org.cubictest.ui.customstep.section.CustomStepSection;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.StackAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class CustomStepEditor extends EditorPart implements ICustomStepListener {

	public static final String CUBIC_TEST_CUSTOM_STEP_EXTENSION = "org.cubictest.exporters";

	private CustomTestStep customStep;

	private boolean isDirty;

	private Map<String, CustomStepSection> sections;

	private CommandStack commandStack = new CommandStack();
	private List<String> stackActionIDs = new ArrayList<String>();
	
	private CommandStackListener commandStackListener = new CommandStackListener(){
		public void commandStackChanged(EventObject event) {
			updateActions(stackActionIDs);
			isDirty = commandStack.isDirty();
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	};

	private ActionRegistry actionRegistry;

	private Text nameText;
	private Text descText;

	private EditDomain editDomain;

	private ParameterTableComposite parameterTableComposite;

	public CustomStepEditor() {
		super();
		sections = new HashMap<String,CustomStepSection>();
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(CUBIC_TEST_CUSTOM_STEP_EXTENSION);
		IExtension[] extensions = extensionPoint.getExtensions();
		// For each extension ...
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			// For each member of the extension ...
			for (IConfigurationElement element : elements) {
				try {
					CustomStepSection section = (CustomStepSection)element.createExecutableExtension("section");
					sections.put(extension.getUniqueIdentifier(),section);
				} catch (CoreException e) {
					ErrorHandler.logAndShowErrorDialog(e);
				}
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Input must be a valid file.");
		}
		
		createActions();
		
		commandStack.addCommandStackListener(commandStackListener);
		commandStack.setUndoLimit(20);
		for(String key : sections.keySet())
			sections.get(key).setCommandStack(commandStack);
		
		getEditDomain().setCommandStack(commandStack);
		
		IFileEditorInput input = (IFileEditorInput) editorInput;

		if (customStep == null) {
			//custom step was not set by reference by e.g. the GTE edit part's double click handler
			customStep = CustomTestStepPersistance.loadFromFile(input.getFile().getLocation().toFile());
		}
		customStep.addCustomStepListener(this);
		
		setSite(site);
		setInput(editorInput);

		setPartName(editorInput.getName());
		
		
		for(String key : sections.keySet()){
			String dataKey = sections.get(key).getDataKey();
			sections.get(key).setData(customStep.getData(dataKey));
			sections.get(key).setProject(input.getFile().getProject());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		commandStack.removeCommandStackListener(commandStackListener);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		CustomTestStepPersistance.saveToFile(customStep,
				((IFileEditorInput) getEditorInput()).getFile());

		try {
			((IFileEditorInput) getEditorInput()).getFile().refreshLocal(1,
					monitor);
		} catch (CoreException e) {
			ErrorHandler
					.logAndShowErrorDialogAndRethrow("Error saving file", e);
		}
		commandStack.markSaveLocation();
		this.isDirty = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setBackground(ColorConstants.white);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		
		createNameTextField(composite);
		createDescriptionTextArea(composite);
		
		parameterTableComposite = new ParameterTableComposite(composite,SWT.NONE);
		parameterTableComposite.setCustemTestStepParameters(customStep.getParameters());
		parameterTableComposite.setCommandStack(commandStack);
		
		new Label(composite, SWT.NONE); //space
		Label exporterInfo = new Label(composite, SWT.NONE);
		exporterInfo.setBackground(ColorConstants.white);
		exporterInfo.setText("Implementation(s) of the step -- click link for new/open:");
		
		for(String key : sections.keySet()){
			sections.get(key).createControl(composite);
		}
	}

	private void createDescriptionTextArea(Composite composite) {
		Composite description = new Composite(composite,SWT.NONE);
		description.setBackground(ColorConstants.white);
		
		FormLayout descLayout = new FormLayout();
		description.setLayout(descLayout);
		
		Label descLabel = new Label(description,SWT.NONE);
		descLabel.setText("Description:");
		descLabel.setBackground(ColorConstants.white);
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0,0);
		layoutData.width = CustomStepSection.STANDARD_LABEL_WIDTH;
		descLabel.setLayoutData(layoutData);
		
		descText = new Text(description,SWT.MULTI|SWT.BORDER);
		descText.setBackground(ColorConstants.white);
		layoutData = new FormData();
		layoutData.left = new FormAttachment(descLabel);
		layoutData.width = CustomStepSection.STANDARD_LABEL_WIDTH * 2;
		layoutData.height = 50;
		descText.setLayoutData(layoutData);
		descText.setText(customStep.getDescription() == null ? "" : customStep.getDescription());		
		
		descText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				int oldPos = descText.getSelection().x;
				if(!descText.getText().equals(customStep.getDescription())){
					ChangeCustomStepDescriptionCommand command = new ChangeCustomStepDescriptionCommand();
					command.setDescription(descText.getText());
					command.setCustomStep(customStep);
					commandStack.execute(command);
				}
				descText.setSelection(oldPos);
			}
		});
	}
	
	
	private void createNameTextField(Composite composite) {
		Composite name = new Composite(composite,SWT.NONE);
		name.setBackground(ColorConstants.white);
		
		FormLayout nameLayout = new FormLayout();
		name.setLayout(nameLayout);
		
		Label nameLabel = new Label(name,SWT.NONE);
		nameLabel.setText("Custom Test Step Name:");
		nameLabel.setBackground(ColorConstants.white);
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0,0);
		layoutData.width = CustomStepSection.STANDARD_LABEL_WIDTH;
		nameLabel.setLayoutData(layoutData);
		
		nameText = new Text(name,SWT.BORDER);
		nameText.setBackground(ColorConstants.white);
		layoutData = new FormData();
		layoutData.left = new FormAttachment(nameLabel);
		layoutData.width = CustomStepSection.STANDARD_LABEL_WIDTH * 2;
		nameText.setLayoutData(layoutData);
		nameText.setText(customStep.getName() == null ? "" : customStep.getName());		
		
		nameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				int oldPos = nameText.getSelection().x;
				if(!nameText.getText().equals(customStep.getName())){
					ChangeCustomStepNameCommand command = new ChangeCustomStepNameCommand();
					command.setName(nameText.getText());
					command.setCustomStep(customStep);
					commandStack.execute(command);
				}
				nameText.setSelection(oldPos);
			}			
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private void updateActions(List<String> actionIds){
		for(int i = 0; i < actionIds.size(); i++){
			IAction action = getActionRegistry().getAction(actionIds.get(i));
			if (action != null && action instanceof UpdateAction){
				((UpdateAction) action).update();
			}
		}
	}
	public ActionRegistry getActionRegistry(){
		if ( actionRegistry == null ){
			actionRegistry = new ActionRegistry();
		}
		return actionRegistry;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {	
		if (adapter == CommandStack.class)
			return commandStack;
		if (adapter == ActionRegistry.class)
			return getActionRegistry();
		return super.getAdapter(adapter);
	}
	
	public EditDomain getEditDomain(){
		if (editDomain == null){
			editDomain = new DefaultEditDomain(this);
		}
		return editDomain;
	}
	
	private void createActions() {
		addStackAction(new UndoAction(this));
		addStackAction(new RedoAction(this));
	}
	/**
	 * Adds a <code>StackAction</code>.
	 * @param action
	 */
	protected void addStackAction(StackAction action){
		getActionRegistry().registerAction(action);
		stackActionIDs.add(action.getId());
	}

	public void handleEvent(CustomTestStepEvent event) {
		String key = event.getKey();
		if (CustomTestStep.DESCRIPTION_CHANGED.equals(key)) {
			String desc = customStep.getDescription();
			if(desc == null)
				desc = "";
			this.descText.setText(desc);
		}
		else if (CustomTestStep.NAME_CHANGED.equals(key)) {
			String name = customStep.getName();
			if(name == null)
				name = "";
			this.nameText.setText(name);
		}
	}

	public void setCustomStep(CustomTestStep customStep) {
		this.customStep = customStep;
		parameterTableComposite.setCustemTestStepParameters(customStep.getParameters());
		for(String key : sections.keySet()){
			String dataKey = sections.get(key).getDataKey();
			sections.get(key).setData(customStep.getData(dataKey));
		}
	}
}
