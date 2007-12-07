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
import java.io.File;

import org.apache.commons.lang.ObjectUtils;
import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.Test;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.persistence.ParameterPersistance;
import org.cubictest.ui.gef.command.ChangeParameterListCommand;
import org.cubictest.ui.gef.command.ChangeParameterListIndexCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.utils.ModelUtil;
import org.cubictest.ui.wizards.NewCubicTestProjectWizard;
import org.cubictest.ui.wizards.NewParamWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class ParameterisationSection extends AbstractPropertySection implements PropertyChangeListener {

	private Test test;
	private Label fileLabel;
	private Text fileName;
	private Button chooseFileButton;
	private Label paramIndexLabel;
	private Spinner paramIndexSpinner;
	private Button openParamsButton;
	private Button refreshParamButton;
	private Button createParamsFileButton;
	
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
			dialog.setTitle("Select a Parameterisation file");
			filter.setPatterns(new String[]{"*.params"});
			
			dialog.addFilter(filter);
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
			if(test.getParamList() != null){
				dialog.setInitialSelection(test.getParamList().getFileName());
			}
			if (dialog.open() == Window.OK) {
				IResource element = (IResource) dialog.getFirstResult();
				if (element == null)
					return;
				String oldName = fileName.getText();
				fileName.setText(element.getFullPath().toString());
				if(!oldName.equals(fileName.getText()))
					filePathChanged();
			}
		}
	};
	
	class FileNameListener implements ModifyListener{
		public void modifyText(ModifyEvent e) {
			String text = fileName.getText();
			ParameterList list = test.getParamList();
			if((list == null && !"".equals(text)) || 
					(list != null && !list.getFileName().equals(text)))
				filePathChanged();
		}
	}

	@Override
	public void createControls(Composite parent, 
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		//parent.setLayout( new GridLayout());
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		fileLabel = getWidgetFactory().createLabel(composite, "Choose parameter file:");
		GridData data = new GridData();
		data.widthHint = STANDARD_LABEL_WIDTH + 70;
		fileLabel.setLayoutData(data);
		data = new GridData();
		//data.horizontalSpan = 2;
		data.widthHint = 300;
		fileName = getWidgetFactory().createText(composite, "");
		fileName.setLayoutData(data);
		FileNameListener fileNameListener = new FileNameListener();
		fileName.addModifyListener(fileNameListener);

		
		data = new GridData();
		data.widthHint = 150;
		
		chooseFileButton = new Button(composite, SWT.NONE);
		chooseFileButton.setText("Browse...");
		chooseFileButton.addSelectionListener(selectionListener);
		chooseFileButton.setLayoutData(data);
		
		paramIndexLabel = getWidgetFactory().createLabel(composite, "Parameter index:");
		paramIndexSpinner = new Spinner(composite, SWT.BORDER);
		paramIndexSpinner.addModifyListener(new ModifyListener(){
			
			public void modifyText(ModifyEvent e) {
				ChangeParameterListIndexCommand command = new ChangeParameterListIndexCommand();
				command.setParameterList(test.getParamList());
				command.setNewIndex(paramIndexSpinner.getSelection());
				command.setTest(test);
				executeCommand(command);
			}
		});

		openParamsButton = getWidgetFactory().createButton(composite, "Open parameter file", SWT.PUSH);
		openParamsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IResource resource = root.findMember(new Path(fileName.getText()));
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, ResourceUtil.getFile(resource), true);
				} catch (PartInitException e) {
					ErrorHandler.logAndShowErrorDialogAndRethrow("Error opening parameter file", e);
				}
			}
		});
		openParamsButton.setLayoutData(data);

				
		data = new GridData();
		data.horizontalSpan = 2;
		createParamsFileButton = getWidgetFactory().createButton(composite, "Create new parameter file", SWT.PUSH);
		createParamsFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewParamWizard wiz = launchNewParamsWizard();
				fileName.setText(wiz.getCreatedFilePath());
				
			}
		});
		createParamsFileButton.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 150;
		data.horizontalSpan = 1;
		refreshParamButton = getWidgetFactory().createButton(composite, "Refresh parameters", SWT.PUSH);
		refreshParamButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filePathChanged();
				refresh();
			}
		});
		refreshParamButton.setLayoutData(data);

		composite.setLayout(gridLayout);
	}
	
	public NewParamWizard launchNewParamsWizard() {
		NewParamWizard wiz = new NewParamWizard();
		String testName = test.getFile().getName();
		String testExt = "." + test.getFile().getFileExtension();
		wiz.setParamsName(testName.substring(0, testName.indexOf(testExt)));
		createParamsFolder();
		wiz.setDefaultDestFolder(getNewParamsPath());
		IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
		wiz.init(workbench, new StructuredSelection(test.getProject()));
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
		dialog.open();
		return wiz;
	}

	private String getNewParamsPath() {
		String path = test.getFile().getParent().getFullPath().toPortableString();
		if (test.getFile().getProjectRelativePath().toString().startsWith(NewCubicTestProjectWizard.TESTS_FOLDER_NAME)) {
			path = path.replaceFirst(NewCubicTestProjectWizard.TESTS_FOLDER_NAME, "params");
		}
		return path;
	}

	private void createParamsFolder() {
		IFolder folder = test.getProject().getFolder("/params");
		if (!folder.exists()) {
			try {
				folder.create(true, true, null);
			} 
			catch (CoreException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow("Error creating params directory", e);
			}
		}
	}
	
	private void updateIndexSpinner() {
		if(test != null && null != test.getParamList()){
			ParameterList list = test.getParamList();
			int length = list.inputParameterSize();
			paramIndexSpinner.setValues(list.getParameterIndex(), 0, 
					(length <= 0) ? 0 : length-1,0, 1, 5);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if(test != null){
			test.removePropertyChangeListener(this);
			if(test.getParamList() != null)
				test.getParamList().removePropertyChangeListener(this);
		}
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof TestEditPart);
		test = (Test) ((TestEditPart) input).getModel();
		test.addPropertyChangeListener(this);
		if(test.getParamList() != null)
			test.getParamList().addPropertyChangeListener(this);
	}
			
	private void filePathChanged() {
		String text = fileName.getText();
		ParameterList list = null;
		try{
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(text));
			if(file.exists())
				list = ParameterPersistance.loadFromFile(file);
		}catch (Exception e) {
			// list will be null
		}
		
		if(!ObjectUtils.equals(list, test.getParamList())){
			ChangeParameterListCommand command = new ChangeParameterListCommand();
			command.setTest(test);
			command.setNewParamList(list);
			command.setOldParamList(test.getParamList());
			executeCommand(command);
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
		boolean visibile = (test.getParamList() != null);
		refreshParamButton.setVisible(visibile);
		paramIndexLabel.setVisible(visibile && test.getParamList().inputParameterSize() > 0);
		paramIndexSpinner.setVisible(visibile && test.getParamList().inputParameterSize() > 0);
		
		if(test.getParamList() != null){
			fileName.setText(test.getParamList().getFileName());
			updateIndexSpinner();
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(event.getOldValue() instanceof ParameterList){
			((ParameterList)event.getOldValue()).removePropertyChangeListener(this);
		}
		if(event.getNewValue() instanceof ParameterList){
			((ParameterList)event.getNewValue()).removePropertyChangeListener(this);
		}
		refresh();
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		test.removePropertyChangeListener(this);
		if(test.getParamList() != null)
			test.getParamList().removePropertyChangeListener(this);
	}
	
}
