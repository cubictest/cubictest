/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewUrlStartPointPage extends WizardPage implements ModifyListener, KeyListener, MouseListener {

	public static final String NAME = "newUrlStartPointPage";
	private Label urlLabel;
	private Label urlExampleLabel;
	private Text urlText;
	private String headerText = "Enter startpoint URL. It is posible to change it later.";
	StartPointTypeSelectionPage startPointTypeSelectionPage;
	private static final int STATUS_OK = 1;
	private static final int STATUS_ERROR = 2;

	protected NewUrlStartPointPage(StartPointTypeSelectionPage startPointTypeSelectionPage) {
		super(NAME);
		setPageComplete(false);
		setMessage(headerText);
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
		urlText.setText("http://");
		urlText.addModifyListener(this);
		urlText.addKeyListener(this);
		urlText.addMouseListener(this);
		urlText.setSelection(urlText.getText().length());
		
		urlExampleLabel = new Label(container, SWT.NONE);
		urlExampleLabel.setText("E.g. http://www.cubictest.org");
		gridData.horizontalSpan = 2;
		urlExampleLabel.setLayoutData(gridData);

		container.setLayout(gridLayout);
		setControl(container);
	}

	public void modifyText(ModifyEvent e) {
		handleUrlInput();
	}

	private void handleUrlInput() {
		if (hasValidUrl()) {
			updateStatus(null, STATUS_OK);
			startPointTypeSelectionPage.urlStartPointSelected = true;
		}
		else {
			updateStatus("URL must start with \"http://\" or \"https://\"", STATUS_ERROR);
		}
	}
	
	public String getUrl(){
		return urlText.getText();
	}

	public boolean hasValidUrl(){
		if (!(urlText.getText().startsWith("http://") || urlText.getText().startsWith("https://"))) {
			return false;
		}
		if (urlText.getText().substring(urlText.getText().indexOf("://") + 3).length() < 1) {
			return false;
		}
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() {
		return null;
	}
	

	private void updateStatus(String message, int severity) {
		if (severity == STATUS_ERROR) {
			setErrorMessage(message);
			setMessage(null);			
			setPageComplete(false);
		}
		else {
			setErrorMessage(null);
			setMessage(headerText );
			setPageComplete(true);
		}
	}
	
	public void setVisible(boolean visible) {
		   super.setVisible(visible);
		   // Set the initial field focus
		   if (visible) {
		      urlText.setFocus();
		   }
		}

	public void keyPressed(KeyEvent e) {
		handleUrlInput();
	}

	public void keyReleased(KeyEvent e) {
		handleUrlInput();
	}

	public void mouseDoubleClick(MouseEvent e) {
		handleUrlInput();
	}

	public void mouseDown(MouseEvent e) {
		handleUrlInput();
	}

	public void mouseUp(MouseEvent e) {
		handleUrlInput();
	}
}
