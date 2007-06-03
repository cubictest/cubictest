package org.cubictest.exporters.cubicunit.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.exporters.cubicunit.CubicUnitRunnerPlugin;
import org.cubictest.exporters.cubicunit.ui.command.ChangeCubicUnitCustomStepCommand;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.cubictest.model.customstep.data.CustomTestStepDataEvent;
import org.cubictest.model.customstep.data.ICustomTestStepDataListener;
import org.cubictest.ui.customstep.section.CustomStepSection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.WorkbenchPart;

public class CustomStepExtension extends CustomStepSection implements ICustomTestStepDataListener{

	private Text classText;
	private Link newClassLink;
	private Button browserClassButton;
	private CustomTestStepData data;
	
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(ColorConstants.white);
		
		FormLayout formLayout = new FormLayout();
		composite.setLayout(formLayout);
		
		newClassLink = new Link(composite, SWT.PUSH);
		newClassLink.setText("<A>CubicUnit Selenium extension*: </A>");
		newClassLink.setBackground(ColorConstants.white);
		newClassLink.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if(data.getPath() != null && data.getPath().length() > 0){
					
					IPath path = Path.fromPortableString(data.getPath());
					IFile file;
					//CubicUnitRunnerPlugin.getDefault().getWorkbench().
					//IDE.openEditor(page, , true);
				}else{
					CustomStepWizard customStepWizard = new CustomStepWizard();
					WizardDialog dialog = new WizardDialog(new Shell(),customStepWizard);
					if(dialog.open() == WizardDialog.OK){
						String className = customStepWizard.getClassName();
						ChangeCubicUnitCustomStepCommand command = 
			        		new ChangeCubicUnitCustomStepCommand();
			        	command.setCustomTestStepData(data);
			        	command.setPath(customStepWizard.getPath());
			        	command.setDisplayText(className);
			        	getCommandStack().execute(command);
					}
				}
			}
		});
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0,0);
		layoutData.width = STANDARD_LABEL_WIDTH;
		newClassLink.setLayoutData(layoutData);
		
		classText = new Text(composite,SWT.BORDER);
		classText.setBackground(ColorConstants.white);
		classText.setText(data.getDisplayText());
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(newClassLink);
		layoutData.width = STANDARD_LABEL_WIDTH * 2;
		classText.setLayoutData(layoutData);
		
		browserClassButton = new Button(composite, SWT.PUSH);
		browserClassButton.setText("Browse...");
		browserClassButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				SelectionDialog dialog;
				try {
					dialog = JavaUI.createTypeDialog(
					        shell, new ProgressMonitorDialog(shell),
					        SearchEngine.createWorkspaceScope(),
					        IJavaElementSearchConstants.CONSIDER_CLASSES, false);
					dialog.setTitle("Open ICustomTestStep implementation");
			        if (dialog.open() == SelectionDialog.OK){
			        	IType result = (IType) dialog.getResult()[0];
			        	
			        	ChangeCubicUnitCustomStepCommand command = 
			        		new ChangeCubicUnitCustomStepCommand();
			        	command.setCustomTestStepData(data);
			        	command.setPath(result.getPath().toPortableString());
			        	command.setDisplayText(result.getFullyQualifiedName());
			        	getCommandStack().execute(command);
			        }
				} catch (JavaModelException ex) {
					ErrorHandler.logAndShowErrorDialog(ex);
				}
		        
			}
		});
		layoutData = new FormData();
		layoutData.left = new FormAttachment(classText,5);
		browserClassButton.setLayoutData(layoutData);
	}
	
	@Override
	public void setData(CustomTestStepData data) {
		this.data = data;
		data.addChangeListener(this);
	}

	public void handleEvent(CustomTestStepDataEvent event) {
		classText.setText(data.getDisplayText());
	}

}
