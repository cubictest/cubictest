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

import org.cubictest.model.Test;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.cubictest.ui.gef.command.AddLanguageCommand;
import org.cubictest.ui.gef.command.ChangeCurrentLanguageCommand;
import org.cubictest.ui.gef.command.RemoveLanguageCommand;
import org.cubictest.ui.gef.command.UpdateLangagesCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.wizards.SelectLanguageInTestWizard;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class I18nSection extends AbstractPropertySection implements PropertyChangeListener {

	private Button languageButton = null;
	
	private Label selectLabel = null;
	private CCombo languageCombo = null;
	private Button removeButton = null;
	private Test test;
	private Button refreshButton;
	
	
	private SelectionListener addListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent event) {
			SelectLanguageInTestWizard wizard = new SelectLanguageInTestWizard(test);
			WizardDialog dlg = new WizardDialog(new Shell(), wizard);
			if(dlg.open() != WizardDialog.CANCEL){	
				String text = wizard.getFileName();
				Language lang  = new Language(ResourcesPlugin.
						getWorkspace().getRoot().getFile(new Path(text)));
				String name = wizard.getLanguageName();
				if(name == null || name.length() < 1)
					return;
				lang.setName(name);
				
				AllLanguages langs = test.getAllLanguages();
				AddLanguageCommand command = new AddLanguageCommand();
				command.setLanguage(lang);
				command.setAllLanguages(langs);
				executeCommand(command);
			}
		}
	};
	private SelectionListener removeListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(test != null && test.getAllLanguages() != null){
				int index = languageCombo.getSelectionIndex();
				if(index >= 0){
					RemoveLanguageCommand command = new RemoveLanguageCommand();
					command.setTest(test);
					command.setAllLanguages(test.getAllLanguages());
					command.setLanguage(test.getAllLanguages().getLanguages().get(index));
					executeCommand(command);
				}
			}
		}
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
        composite.setLayout(gridLayout);
        
        GridData labelData = new GridData();
        labelData.widthHint = STANDARD_LABEL_WIDTH + 70;
        
        GridData inputData = new GridData();
		inputData.widthHint = 300;
        
		GridData buttonData = new GridData();
		buttonData.widthHint = 150;
		
		
        selectLabel = getWidgetFactory().createLabel(composite,"Set language");
        selectLabel.setLayoutData(labelData);

        languageCombo = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY | SWT.BORDER);
        languageCombo.addSelectionListener(new SelectionAdapter(){
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		int index = languageCombo.getSelectionIndex();
        		Language lang = test.getAllLanguages().getLanguages().get(index);
        		ChangeCurrentLanguageCommand command = new ChangeCurrentLanguageCommand();
        		command.setAllLanguages(test.getAllLanguages());
        		command.setCurrentLanguage(lang);
        		command.setTest(test);
        		executeCommand(command);
        	}
        });
		updateLanguageCombo();
		languageCombo.setLayoutData(inputData);

		languageButton = getWidgetFactory().createButton(composite,"Add language...", SWT.PUSH);
		languageButton.addSelectionListener(addListener);
		languageButton.setLayoutData(buttonData);
		
		getWidgetFactory().createLabel(composite,"");
		getWidgetFactory().createLabel(composite,"");
		
        removeButton = getWidgetFactory().createButton(composite, "Remove language", SWT.PUSH);
        removeButton.setLayoutData(buttonData);
        removeButton.addSelectionListener(removeListener );
        
        getWidgetFactory().createLabel(composite,"");
        getWidgetFactory().createLabel(composite,"");

        refreshButton = getWidgetFactory().createButton(composite, "Refresh languages", SWT.PUSH);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UpdateLangagesCommand command = new UpdateLangagesCommand();
				command.addTest(test);
				if (MessageDialog.openConfirm(new Shell(), "Confirm refreshing languages", 
						"This action is irreversible. Do you want to continue?"))
					executeCommand(command);
			}
		});
		refreshButton.setLayoutData(buttonData);
		
        //composite.setSize(new Point(751, 305));
	}
	
	private void executeCommand(Command command) {
		if (getPart() instanceof GraphicalTestEditor) {
			GraphicalTestEditor gte = (GraphicalTestEditor) getPart();
			gte.getCommandStack().execute(command);
		}
	}
	
	private void updateLanguageCombo() {
		if(test != null && test.getAllLanguages() != null){
			AllLanguages langs = test.getAllLanguages();
			languageCombo.removeAll();
			for(Language lang : langs.getLanguages()){
				languageCombo.add(lang.getName() + " - " + lang.getFileName());
			}
			int index = langs.getLanguages().indexOf(langs.getCurrentLanguage());
			languageCombo.select(index);
		}
	}


	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if(test != null){
			test.getAllLanguages().removePropertyChangeListener(this);
		}
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof TestEditPart);
		test = (Test) ((TestEditPart) input).getModel();
		AllLanguages langs = test.getAllLanguages();
		langs.addPropertyChangeListener(this);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		updateLanguageCombo();
	}

	public void propertyChange(PropertyChangeEvent event) {
		refresh();
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		if(test != null){
			test.getAllLanguages().removePropertyChangeListener(this);
		}
	}
}
