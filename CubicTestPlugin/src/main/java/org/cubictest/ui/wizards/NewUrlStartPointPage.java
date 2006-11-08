/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewUrlStartPointPage extends WizardPage implements ModifyListener {

	public static final String NAME = "newUrlStartPointPage";
	private Label urlLabel;
	private Label urlExampleLabel;
	private Text urlText;
	private String description = "Enter startpoint URL";
	private String errorMessage = "Enter an URL - it is posible to change it later.";
	StartPointTypeSelectionPage startPointTypeSelectionPage;

	protected NewUrlStartPointPage(StartPointTypeSelectionPage startPointTypeSelectionPage) {
		super(NAME);
		setPageComplete(false);
		setMessage(description );
		this.startPointTypeSelectionPage = startPointTypeSelectionPage;
	}

	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		
		GridLayout gridLayout = new GridLayout(2,false);
		gridLayout.numColumns = 2;
		
		urlLabel = new Label(container, SWT.LEFT);
		urlLabel.setText("URL to the site/page you want to test:");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		urlText = new Text(container, SWT.SINGLE | SWT.BORDER);
		urlText.setLayoutData(gridData);
		urlText.addModifyListener(this);
		
		urlExampleLabel = new Label(container, SWT.NONE);
		urlExampleLabel.setText("E.g. http://www.cubictest.org");
		gridData.horizontalSpan = 2;
		urlExampleLabel.setLayoutData(gridData);

		container.setLayout(gridLayout);
		setControl(container);
	}

	public void modifyText(ModifyEvent e) {
		setPageComplete(getText().length() > 3);
		setErrorMessage(getText().length() < 3 ? errorMessage : null);
		startPointTypeSelectionPage.urlStartPointSelected = true;
	}

	public String getText(){
		if(urlText != null)
			return urlText.getText();
		else 
			return ""; 
	}
	
	
	@Override
	public IWizardPage getNextPage() {
		return null;
	}
	
}
