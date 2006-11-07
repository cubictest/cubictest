/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.wizards;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Page;
import org.eclipse.jface.wizard.Wizard;


/**
 * @author SK Skytteren
 * Wizard for creating a new abstract page.
 */
public class NewCubicTestAbstractPageWizard extends Wizard {

	private WizardNewAbstractPageCreationPage abstractPage;
	private AbstractPage page;
	/**
	 * @param transition
	 */
	public NewCubicTestAbstractPageWizard(AbstractPage page) {
		super();
		this.page = page;
		setNeedsProgressMonitor(true);
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		abstractPage = new WizardNewAbstractPageCreationPage(page);
		if (page instanceof Page){
			abstractPage.setTitle("New CubicTest Page");
			abstractPage.setDescription("Set the Name of the new Page");
		}else {
			abstractPage.setTitle("New CubicTest Common");
			abstractPage.setDescription("Set the Name of the new Common");
		}
		addPage(abstractPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish(){
		page.setName(abstractPage.getText());
		return true;
	}

}
