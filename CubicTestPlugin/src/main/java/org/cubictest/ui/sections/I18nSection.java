/*
 * Created on 17.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class I18nSection extends AbstractPropertySection implements PropertyChangeListener {

	private Label fileLabel = null;
	private Text fileName = null;
	private Button browseButton = null;
	private Button languageButton = null;
	private Text languageText = null;
	private Label languageLabel = null;
	private Label selectLabel = null;
	private CCombo languageCombo = null;
	private Label removeLabel = null;
	private CCombo removeFile = null;
	private Button removeButton = null;
	private Test test;
	private Button refreshButton;
	
	ResourcePatternFilter filter = new ResourcePatternFilter(){
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IFolder)
				return true;
			if (element instanceof IProject)
				return true;
			return !super.select(viewer, parentElement, element);
		}
	};
	
	SelectionListener selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog =
				new ElementTreeSelectionDialog(new Shell(), 
							new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle("Select a .properties file");
			filter.setPatterns(new String[]{"*.properties"});
			
			dialog.addFilter(filter);
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
			if(test.getParamList() != null){
				dialog.setInitialSelection(test.getParamList().getFileName());
			}
			if (dialog.open() == Window.OK) {
				IResource element= (IResource) dialog.getFirstResult();
				fileName.setText(element.getFullPath().toString());
			}
		}
	};
	
	private SelectionListener addListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent event) {
			String text = fileName.getText();
			Language lang  = new Language(ResourcesPlugin.
				getWorkspace().getRoot().getFile(new Path(text)));
			String name = languageText.getText();
			if(name == null || name.length() < 1)
				return;
			lang.setName(name);
			
			AllLanguages langs = test.getAllLanguages();
			AddLanguageCommand command = new AddLanguageCommand();
			command.setLanguage(lang);
			command.setAllLanguages(langs);
			executeCommand(command);
			
			fileName.setText("");
			languageText.setText("");
		}
	};
	private SelectionListener removeListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(test != null && test.getAllLanguages() != null){
				int index = removeFile.getSelectionIndex();
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
		
        fileLabel = getWidgetFactory().createLabel(composite, 
        		"Internationalisation file:");
        fileLabel.setLayoutData(labelData);
        
        fileName = getWidgetFactory().createText(composite, "");
        fileName.setLayoutData(inputData);
        
        browseButton = getWidgetFactory().createButton(composite,"Browse...", SWT.PUSH);
        browseButton.addSelectionListener(selectionListener); 
        browseButton.setLayoutData(buttonData);
        
        languageLabel = getWidgetFactory().createLabel(composite, "Language");
        languageLabel.setLayoutData(labelData);
        
        languageText = getWidgetFactory().createText(composite, "");
		languageText.setLayoutData(inputData);

		languageButton = getWidgetFactory().createButton(composite,"Add", SWT.PUSH);
		languageButton.addSelectionListener(addListener);
		languageButton.setLayoutData(buttonData);
		
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
				
        getWidgetFactory().createLabel(composite, "");
        
        removeLabel = getWidgetFactory().createLabel(composite, "Remove language:");
        removeLabel.setLayoutData(labelData);
        
        removeFile = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY | SWT.BORDER);
        removeFile.setLayoutData(inputData);
        
        removeButton = getWidgetFactory().createButton(composite, "Remove", SWT.PUSH);
        removeButton.setLayoutData(buttonData);
        removeButton.addSelectionListener(removeListener );
        
        getWidgetFactory().createLabel(composite, "");
        getWidgetFactory().createLabel(composite, "");
        
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
			removeFile.removeAll();
			for(Language lang : langs.getLanguages()){
				languageCombo.add(lang.getName() + " - " + lang.getFileName());
				removeFile.add(lang.getName() + " - " + lang.getFileName());
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
