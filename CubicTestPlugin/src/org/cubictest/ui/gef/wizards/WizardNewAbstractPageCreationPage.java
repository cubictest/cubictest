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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author SK Skytteren
 * Wizard page for creating an new Abstract Page.
 */
public class WizardNewAbstractPageCreationPage extends WizardPage{

	private Text text;	
	private AbstractPage page;
	
	/**
	 * 
	 * @param page
	 */
	public WizardNewAbstractPageCreationPage(AbstractPage page) {
		super("Set Name");
		this.page = page;
		setPageComplete(true);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 4;
		content.setLayout(layout);
		
		Label fill = new Label(content, SWT.NULL);
		if (page instanceof Page)
			fill.setText("Set the Page name:");
		else
			fill.setText("Set the Common name:");
		
		text = new Text(content, SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setControl(content);
	}
	
	public String getText(){
		return text.getText();
	}
}
