/*
 * Created on 17.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.sections;

import org.cubictest.model.Test;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class I18nSection extends AbstractPropertySection {

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
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog =
				new ElementTreeSelectionDialog(new Shell(), 
							new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle("Select a .properties file");
			filter.setPatterns(new String[]{"*.properties"});
			
			dialog.addFilter(filter);
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
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
			
			AllLanguages langs = test.getAllLanuages();
			if(langs == null){
				langs = new AllLanguages();
				test.setAllLanuages(langs);
			}
			langs.addLanguage(lang);
			
			fileName.setText("");
			languageText.setText("");
			
			refresh();
		}
	};
	private SelectionListener removeListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(test != null && test.getAllLanuages() != null){
				AllLanguages langs = test.getAllLanuages();
				int index = removeFile.getSelectionIndex();
				if(index >= 0){
					langs.getLanguages().remove(index);
					updateLanguageCombo();
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
        labelData.widthHint = STANDARD_LABEL_WIDTH + 50;
        
        GridData inputData = new GridData();
		inputData.widthHint = 300;
        
		GridData buttonData = new GridData();
		buttonData.widthHint = 100;
		
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
        		AllLanguages langs = test.getAllLanuages();
        		Language lang = langs.getLanguages().get(index);
        		langs.setCurrentLanguage(lang);
        		test.updateObservers();
        		updateLanguageCombo();
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
        
        composite.setSize(new Point(751, 305));
	}
	
	
	private void updateLanguageCombo() {
		if(test != null && test.getAllLanuages() != null){
			AllLanguages langs = test.getAllLanuages();
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
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof TestEditPart);
		test = (Test) ((TestEditPart) input).getModel();
	}
	
	@Override
	public void refresh() {
		super.refresh();
		updateLanguageCombo();
	}
}
