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

import org.cubictest.common.utils.ErrorHandler;
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class I18nSection extends AbstractPropertySection implements PropertyChangeListener {

	private Button addLanguageButton = null;
	private Button removeButton = null;
	private Button refreshButton;
	private Button openI18nFileButton;

	private Label selectLabel = null;
	private CCombo languageCombo = null;
	private Test test;
	
	private Label noI18nValuesLabel;
	private Label noObserversLabel;
	
	private SelectionListener addListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent event) {
			SelectLanguageInTestWizard wizard = new SelectLanguageInTestWizard(test);
			WizardDialog dlg = new WizardDialog(new Shell(), wizard);
			if(dlg.open() != WizardDialog.CANCEL){	
				String text = wizard.getFileName();
				Language lang = new Language(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(text)));
				String name = wizard.getLanguageName();
				if(name == null || name.length() < 1) {
					return;
				}
				lang.setName(name);
				
				AllLanguages langs = test.getAllLanguages();
				AddLanguageCommand command = new AddLanguageCommand();
				command.setLanguage(lang);
				command.setAllLanguages(langs);
				executeCommand(command);
				refresh();
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
					languageCombo.select(0);
					refresh();
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
        labelData.widthHint = STANDARD_LABEL_WIDTH + 30;
        
        GridData languageSelectData = new GridData();
		languageSelectData.widthHint = 400;
        
		GridData buttonData = new GridData();
		buttonData.widthHint = 150;
		
		
        selectLabel = getWidgetFactory().createLabel(composite,"Set language:  ");
        selectLabel.setLayoutData(labelData);

        languageCombo = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY | SWT.BORDER);
        languageCombo.setLayoutData(languageSelectData);
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
				refresh();
        	}
        });
		updateLanguageCombo();
		
		
		openI18nFileButton = getWidgetFactory().createButton(composite, "Open file", SWT.PUSH);
		openI18nFileButton.setLayoutData(buttonData);
		openI18nFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IResource resource = root.findMember(new Path(test.getAllLanguages().getCurrentLanguage().getFileName()));
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, ResourceUtil.getFile(resource), true);
				} catch (PartInitException e) {
					ErrorHandler.logAndShowErrorDialogAndRethrow("Error opening internationalisation file", e);
				}
			}
		});

		addLanguageButton = getWidgetFactory().createButton(composite,"Add language...", SWT.PUSH);
		addLanguageButton.addSelectionListener(addListener);
		addLanguageButton.setLayoutData(buttonData);
		

        refreshButton = getWidgetFactory().createButton(composite, "Refresh languages", SWT.PUSH);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UpdateLangagesCommand command = new UpdateLangagesCommand();
				command.addTest(test);
//				if (MessageDialog.openConfirm(new Shell(), "Confirm refreshing languages", 
//						"\"Undo\" is not supported for this operation. Do you want to continue?"))
					executeCommand(command);
					refresh();
			}
		});
		refreshButton.setLayoutData(buttonData);

        removeButton = getWidgetFactory().createButton(composite, "Remove language", SWT.PUSH);
        removeButton.setLayoutData(buttonData);
        removeButton.addSelectionListener(removeListener );
        
        
        GridData statusData = new GridData();
		statusData.horizontalSpan = 3;
		noI18nValuesLabel = getWidgetFactory().createLabel(composite, "Status: No text values in internationalisation file. Add content, save file and press \"Refresh languages\"");
		noI18nValuesLabel.setLayoutData(statusData);
		noI18nValuesLabel.setVisible(false);
		noObserversLabel = getWidgetFactory().createLabel(composite, "Status: No page elements has internationalisation enabled. " +
				"To enable, check \"Use internationalisation\" on page element identifiers.");
		noObserversLabel.setLayoutData(statusData);
		noObserversLabel.setVisible(false);

}
	
	private void executeCommand(Command command) {
		if (getPart() instanceof GraphicalTestEditor) {
			GraphicalTestEditor gte = (GraphicalTestEditor) getPart();
			gte.getCommandStack().execute(command);
		}
	}
	
	private void updateLanguageCombo() {
		if(test != null && test.getAllLanguages() != null){
			AllLanguages allLanguages = test.getAllLanguages();
			languageCombo.removeAll();
			for(Language lang : allLanguages.getLanguages()){
				languageCombo.add(lang.getName() + " - " + lang.getFileName());
			}
			int i = 0;
			for (Language language : allLanguages.getLanguages()) {
				if (language.relaxedEqual(allLanguages.getCurrentLanguage())) {
					languageCombo.select(i);
					return;
				}
				i++;
			}
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
		AllLanguages allLanguages = test.getAllLanguages();
		
		if (allLanguages.isEmpty()) {
			removeButton.setVisible(false);
			refreshButton.setVisible(false);
			openI18nFileButton.setVisible(false);
			selectLabel.setVisible(false);
			languageCombo.setVisible(false);

		}
		else {
			removeButton.setVisible(true);
			refreshButton.setVisible(true);
			openI18nFileButton.setVisible(true);
			selectLabel.setVisible(true);
			languageCombo.setVisible(true);
		}
		
		Language currentLanguage = allLanguages.getCurrentLanguage();
		if(currentLanguage != null) {
			if (currentLanguage.isEmpty()) {
				noI18nValuesLabel.setVisible(true);
				noObserversLabel.setVisible(false);
			}
			else if (!allLanguages.hasObservers()) {
				noI18nValuesLabel.setVisible(false);
				noObserversLabel.setVisible(true);
			}
			else {
				noI18nValuesLabel.setVisible(false);
				noObserversLabel.setVisible(false);
			}
		}
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
