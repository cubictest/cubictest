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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

		customStep = CustomTestStepPersistance.loadFromFile(input.getFile());
		customStep.addCustomStepListener(this);
		
		setSite(site);
		setInput(editorInput);

		setPartName(editorInput.getName());
		
		
		for(String key : sections.keySet()){
			String dataKey = sections.get(key).getDataKey();
			sections.get(key).setData(customStep.getData(dataKey));
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
		descText.setText(customStep.getDescription());		
		
		descText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				if(!descText.getText().equals(customStep.getDescription())){
					ChangeCustomStepDescriptionCommand command = new ChangeCustomStepDescriptionCommand();
					command.setDescription(descText.getText());
					command.setCustomStep(customStep);
					commandStack.execute(command);
				}
			}			
		});
		
		parameterTableComposite = new ParameterTableComposite(composite,SWT.NONE);
		parameterTableComposite.setCustemTestStepParameters(customStep.getParameters());
		parameterTableComposite.setCommandStack(commandStack);
		
		for(String key : sections.keySet()){
			sections.get(key).createControl(composite);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private void updateActions(List actionIds){
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
		String desc = customStep.getDescription();
		if(desc == null)
			desc = "";
		this.descText.setText(desc);
	}
}
