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
package org.cubictest.ui.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.persistence.ParameterPersistance;
import org.cubictest.ui.gef.command.ChangeParameterListCommand;
import org.cubictest.ui.gef.command.ChangeSubTestParamIndexCommand;
import org.cubictest.ui.gef.controller.SubTestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section for parameterization of sub tests.
 * 
 * @author Christian Schwarz
 */
public class SubTestParamsSection extends AbstractPropertySection implements PropertyChangeListener {

	private Test test;
	private ParameterList paramList;
	private Label paramIndexLabel;
	private Button useParamIndexFromTestCheckbox;
	private Label noParamsLabel;
	private Spinner paramIndexSpinner;
	private Label useParamIndexFromTestLabel;
	private SubTest subtest;
	private Button openParamsButton;
	private Button refreshParamButton;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		
		createNoParamsLabel(composite);
		createIndexSpinner(composite);
		createUseIndexDefinedInTestCheckbox(composite);
		createOpenParamsButton(composite);
		createRefreshParamsButton(composite);
		
		composite.setLayout(gridLayout);
	}

	private void createRefreshParamsButton(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		refreshParamButton = getWidgetFactory().createButton(composite, "Refresh parameters", SWT.PUSH);
		refreshParamButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshParamsFromDisk();
			}

		});
		refreshParamButton.setLayoutData(data);
	}

	private void refreshParamsFromDisk() {
		test = subtest.getTest(true);
		paramList = test.getParamList();
		if (paramList != null) {
			IFile paramFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(paramList.getFileName()));
			if(paramFile.exists()) {
				ChangeParameterListCommand command = new ChangeParameterListCommand();
				command.setTest(test);
				command.setNewParamList(ParameterPersistance.loadFromFile(paramFile));
				command.setOldParamList(paramList);
				executeCommand(command);
			}
			refresh();
		}
	}

	
	private void createUseIndexDefinedInTestCheckbox(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		useParamIndexFromTestLabel = getWidgetFactory().createLabel(composite, "Use parameter index as defined in test:");
		useParamIndexFromTestCheckbox = getWidgetFactory().createButton(composite, "", SWT.CHECK);
		useParamIndexFromTestCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if (useParamIndexFromTestCheckbox.getSelection()) {
					//use param defined in test
					paramIndexSpinner.setEnabled(false);
					ChangeSubTestParamIndexCommand command = new ChangeSubTestParamIndexCommand();
					command.setNewIndex(-1);
					command.setTest(subtest);
					executeCommand(command);
				}
				else {
					//use param defined in spinner
					paramIndexSpinner.setEnabled(true);
					ChangeSubTestParamIndexCommand command = new ChangeSubTestParamIndexCommand();
					command.setNewIndex(paramIndexSpinner.getSelection());
					command.setTest(subtest);
					executeCommand(command);
				}
			}
		});
		useParamIndexFromTestLabel.setLayoutData(data);
	}

	private void createNoParamsLabel(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 3;
		noParamsLabel = getWidgetFactory().createLabel(composite, "Test in subtest has no parameterisation configured");
		noParamsLabel.setLayoutData(data);
	}

	private void createIndexSpinner(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		paramIndexLabel = getWidgetFactory().createLabel(composite, "Parameter index:");

		paramIndexSpinner = new Spinner(composite, SWT.BORDER);
		paramIndexSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (subtest.getParameterIndex() != paramIndexSpinner.getSelection()) {
					ChangeSubTestParamIndexCommand command = new ChangeSubTestParamIndexCommand();
					command.setNewIndex(paramIndexSpinner.getSelection());
					command.setTest(subtest);
					executeCommand(command);
				}
			}
		});
		paramIndexSpinner.setLayoutData(data);
	}

	private void createOpenParamsButton(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		openParamsButton = getWidgetFactory().createButton(composite, "Open test parameter file", SWT.PUSH);
		openParamsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ViewUtil.openFileForViewing(test.getParamList().getFileName());
			}
		});
		openParamsButton.setLayoutData(data);
	}
	
	
	private void updateIndexSpinner() {
		if(test != null && test.getParamList() != null){
			ParameterList list = test.getParamList();
			int length = list.inputParameterSize();
			paramIndexSpinner.setValues(subtest.getParameterIndex(), 0, (length <= 0) ? 0 : length - 1, 0, 1, 5);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof SubTestEditPart);
		
		subtest = (SubTest) ((SubTestEditPart) input).getModel();
		boolean testWasNull = (test == null);
		test = subtest.getTest(false);
		paramList = test.getParamList();

		if (testWasNull) {
			refreshParamsFromDisk();
		}
	}
			
	private void executeCommand(Command command) {
		IWorkbenchPart part = getPart();
		if(part instanceof GraphicalTestEditor)
			((GraphicalTestEditor)part).getCommandStack().execute(command);
		
	}
	
	@Override
	public void refresh() {
		super.refresh();
		
		if (test.hasParamsConfigured()) {
			paramIndexSpinner.setVisible(true);
			paramIndexLabel.setVisible(true);
			openParamsButton.setVisible(true);
			noParamsLabel.setVisible(false);
			useParamIndexFromTestCheckbox.setVisible(true);
			useParamIndexFromTestLabel.setVisible(true);
		}
		else {
			paramIndexSpinner.setVisible(false);
			paramIndexLabel.setVisible(false);
			openParamsButton.setVisible(false);
			noParamsLabel.setVisible(true);
			useParamIndexFromTestCheckbox.setVisible(false);
			useParamIndexFromTestLabel.setVisible(false);
		}
		
		if (subtest != null && subtest.getParameterIndex() < 0) {
			useParamIndexFromTestCheckbox.setSelection(true);
			paramIndexSpinner.setEnabled(false);
		}

		if(test.getParamList() != null) {
			updateIndexSpinner();
		}
	}


	public void propertyChange(PropertyChangeEvent event) {
		refresh();
	}
}
