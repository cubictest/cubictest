package org.cubictest.ui.wizards;

import org.cubictest.model.Test;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

public class WizardSelectLanuageInTestPage extends WizardPage {

	private Label fileLabel = null;
	private Text fileName = null;
	private Button browseButton = null;
	private Text languageText = null;
	private Label languageLabel = null;
	
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
				if(fileName.getText().length() > 0){
					languageText.setEnabled(true);
					languageText.setFocus();
				}
			}
		}
	};
	private Test test;
	
	
	protected WizardSelectLanuageInTestPage(Test test) {
		super("selectLanguaePage");
		this.test = test;
		
		setTitle("Add/Create Internationalisation File");
		setDescription("Select or create an internationalisation file (*.properites) to add to" +
				" this test\n and set its lanuage name.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		GridData labelData = new GridData();
        labelData.widthHint = 120;
        
        GridData inputData = new GridData();
		inputData.widthHint = 300;
        
		GridData buttonData = new GridData();
		buttonData.widthHint = 150;

		fileLabel = new Label(container, SWT.NONE);
		fileLabel.setText("Use existing file:");
		fileLabel.setLayoutData(labelData);

		fileName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		fileName.setLayoutData(inputData);
	
		browseButton = new Button(container,SWT.PUSH);
		browseButton.setText("Browse..."); 
		browseButton.addSelectionListener(selectionListener);
		browseButton.setLayoutData(buttonData);

		
		GridData largeButtonData = new GridData();
		largeButtonData.horizontalSpan = 3;
		Button newLanguageButton = new Button(container, SWT.PUSH);
		newLanguageButton.setLayoutData(largeButtonData);
		newLanguageButton.setText("Create new internationalisation file");
		newLanguageButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewPropertiesWizard wizard = new NewPropertiesWizard();
				WizardDialog dialog = new WizardDialog(new Shell(), wizard);
				if(dialog.open() != Dialog.CANCEL){
					fileName.setText(wizard.getContainerName() + "/" + wizard.getFileName());
					languageText.setEnabled(true);
					languageText.setFocus();
				}
			}
		});

		GridData spaceData = new GridData();
		spaceData.horizontalSpan = 3;
		new Label(container,SWT.NONE).setLayoutData(spaceData);

		
		languageLabel = new Label(container,SWT.NONE);
		languageLabel.setText("Language name:"); 
		languageLabel.setLayoutData(labelData);
		 
		languageText = new Text(container, SWT.BORDER);
		languageText.setEnabled(false);
		languageText.setLayoutData(inputData);
		
		
		
		
		setControl(container);
	}

	public String getFileName() {
		return fileName.getText();
	}

	public String getLanguageName() {
		return languageText.getText();
	}

}
