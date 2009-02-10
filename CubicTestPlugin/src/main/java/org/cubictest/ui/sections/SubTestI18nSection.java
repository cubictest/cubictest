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

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.cubictest.ui.gef.command.ChangeCurrentLanguageCommand;
import org.cubictest.ui.gef.command.ChangeSubTestLanguageCommand;
import org.cubictest.ui.gef.controller.SubTestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section for i18n of sub tests.
 * 
 * @author Christian Schwarz
 */
public class SubTestI18nSection extends AbstractPropertySection implements PropertyChangeListener {

	private Test test;
	private AllLanguages allLanguages;
	private Label selectLabel;
	private Button useLanguageFromTestCheckbox;
	private Label noi18nLabel;
	private CCombo languageCombo = null;
	private Label useLanguageFromTestLabel;
	private SubTest subtest;
	private Button openLanguageFileButton;
	private Button refreshButton;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.horizontalSpacing = 15;
		composite.setLayout(gridLayout);
		
		createNoI18nLabel(composite);
		createLanguageCombo(composite);
		createUseLanguageDefinedInTestCheckbox(composite);
		createOpenLanguageFileButton(composite);
		createRefreshButton(composite);
		
	}

	private void createRefreshButton(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 3;
		refreshButton = getWidgetFactory().createButton(composite, "Refresh", SWT.PUSH);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshI18nFromDisk();
			}

		});
		refreshButton.setLayoutData(data);
	}

	private void refreshI18nFromDisk() {
		test = subtest.getTest(true);
		allLanguages = test.getAllLanguages();
		if (allLanguages != null) {
			refresh();
		}
	}

	
	private void createUseLanguageDefinedInTestCheckbox(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		useLanguageFromTestLabel = getWidgetFactory().createLabel(composite, "Use language as defined in test:");
		useLanguageFromTestCheckbox = getWidgetFactory().createButton(composite, "", SWT.CHECK);
		useLanguageFromTestCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if (useLanguageFromTestCheckbox.getSelection()) {
					//use language defined in test
					languageCombo.setEnabled(false);
					ChangeSubTestLanguageCommand command = new ChangeSubTestLanguageCommand();
					command.setNewLanguage(null);
					command.setTest(subtest);
					executeCommand(command);
				}
				else {
					languageCombo.setEnabled(true);
					ChangeSubTestLanguageCommand command = new ChangeSubTestLanguageCommand();
					command.setNewLanguage(test.getAllLanguages().getCurrentLanguage());
					command.setTest(subtest);
					executeCommand(command);
				}
				refresh();
			}
		});
		useLanguageFromTestLabel.setLayoutData(data);
	}

	private void createNoI18nLabel(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 3;
		noi18nLabel = getWidgetFactory().createLabel(composite, "Test in subtest has no internationalisation configured. Enable it in the test and click \"Refresh\"");
		noi18nLabel.setLayoutData(data);
	}

	private void createLanguageCombo(Composite composite) {
        selectLabel = getWidgetFactory().createLabel(composite,"Set language:");
		selectLabel.setLayoutData(new GridData());

        languageCombo = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY | SWT.BORDER);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        data.widthHint = 400;
        languageCombo.setLayoutData(data);
        languageCombo.addSelectionListener(new SelectionAdapter(){
        	@Override
        	public void widgetSelected(SelectionEvent e) {
				ChangeSubTestLanguageCommand command = new ChangeSubTestLanguageCommand();
				command.setNewLanguage(getSelectedLanguage());
				command.setTest(subtest);
				executeCommand(command);
				refresh();
        	}

        });
		updateLanguageCombo();
	}

	private Language getSelectedLanguage() {
		int index = languageCombo.getSelectionIndex();
		Language lang = test.getAllLanguages().getLanguages().get(index);
		return lang;
	}

	private void createOpenLanguageFileButton(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 3;
		openLanguageFileButton = getWidgetFactory().createButton(composite, "Open test internationalisation file", SWT.PUSH);
		openLanguageFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ViewUtil.openFileForViewing(getSelectedLanguage().getFileName());
			}
		});
		openLanguageFileButton.setLayoutData(data);
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
		allLanguages = test.getAllLanguages();

		if (testWasNull) {
			refreshI18nFromDisk();
		}
		updateLanguageCombo();
	}
			
	private void executeCommand(Command command) {
		IWorkbenchPart part = getPart();
		if(part instanceof GraphicalTestEditor)
			((GraphicalTestEditor)part).getCommandStack().execute(command);
		
	}
	
	@Override
	public void refresh() {
		super.refresh();
		
		if (test.hasI18nConfigured()) {
			languageCombo.setVisible(true);
			selectLabel.setVisible(true);
			openLanguageFileButton.setVisible(true);
			noi18nLabel.setVisible(false);
			useLanguageFromTestCheckbox.setVisible(true);
			useLanguageFromTestLabel.setVisible(true);
		}
		else {
			languageCombo.setVisible(false);
			selectLabel.setVisible(false);
			openLanguageFileButton.setVisible(false);
			noi18nLabel.setVisible(true);
			useLanguageFromTestCheckbox.setVisible(false);
			useLanguageFromTestLabel.setVisible(false);
		}
		
		if (subtest != null && subtest.getLanguage() == null) {
			useLanguageFromTestCheckbox.setSelection(true);
			languageCombo.setEnabled(false);
		}

		if(test.getAllLanguages() != null) {
			updateLanguageCombo();
		}
	}


	public void propertyChange(PropertyChangeEvent event) {
		refresh();
	}
	
	private void updateLanguageCombo() {
		if(test != null && test.getAllLanguages() != null){
			AllLanguages testLanguages = test.getAllLanguages();
			languageCombo.removeAll();
			for(Language language : testLanguages.getLanguages()){
				languageCombo.add(language.getName() + " - " + language.getFileName());
			}
			int i = 0;
			for (Language language : testLanguages.getLanguages()) {
				if (language.relaxedEqual(subtest.getLanguage())) {
					languageCombo.select(i);
					return;
				}
				i++;
			}
		}
	}
}
