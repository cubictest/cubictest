package org.cubictest.exporters.cubicunit.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class CustomStepWizard extends Wizard {

	
	private NewClassWizardPage classWizard;
	private String name;
	
	public CustomStepWizard() {
		setWindowTitle("Create new CustomTestStep");
	}
	
	@Override
	public void addPages() {
		classWizard = new NewClassWizardPage();
		classWizard.addSuperInterface("org.cubictest.custom.ICustomTestStep");
		addPage(classWizard);
	}
	
	@Override
	public boolean performFinish() {
		name = classWizard.getTypeName();
		if(name != null && name.length() > 0){
			try {
				classWizard.createType(new NullProgressMonitor());
				return true;
			} catch (CoreException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			} catch (InterruptedException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			}
		}
		return false;
	}
	
	public String getClassName(){
		String packageText = classWizard.getPackageText();
		String typeTypeName = classWizard.getTypeName();
		return packageText + (packageText.length() > 0 ? "." : "") + typeTypeName;
	}

	public String getPath() {
		return classWizard.getModifiedResource().getProjectRelativePath().toPortableString();
	}

}
