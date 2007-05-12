package org.cubictest.exporters.cubicunit.ui;

import org.cubictest.exporters.cubicunit.CubicUnitRunnerPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetValuesPage extends WizardPage {

	private static final String CUBIC_UNIT_RUNNER_BROWSER_TYPE = "CubicUnitRunnerBrowserType";
	private static final String CUBIC_UNIT_RUNNER_PORT_NUMBER = "CubicUnitRunnerPortNumber";
	private static final String PLEASE_ENTER_A_PORT_NUMBER = "Enter SeleniumServer port number it the current doesn't work";
	private Label portLabel;
	private Text portText;
	private int port;
	private Label browserLabel;
	private Combo browserCombo;
	private BrowserType browserType;

	protected SetValuesPage() {
		super("Set CubicSeleniumServerPort");
		port = 4444;
		
	}

	public void createControl(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NULL);
		
		GridData gridData = new GridData();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		browserLabel = new Label(content, SWT.NONE);
		browserLabel.setText("Label");
		createBrowserCombo(content);
		
		portLabel = new Label(content, SWT.NONE);
		portLabel.setText("Port number:");
		portLabel.setLayoutData(gridData);
		portText = new Text(content, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 50;
		portText.setLayoutData(gridData);
		portText.setTextLimit(5);
		
		try{
			port = CubicUnitRunnerPlugin.getDefault().
				getDialogSettings().getInt(CUBIC_UNIT_RUNNER_PORT_NUMBER);
		}catch(NumberFormatException ex){
		}
		portText.setText(port + "");
		portText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				try{
					port = Integer.parseInt(portText.getText());
					setPageComplete(true);
					setMessage(PLEASE_ENTER_A_PORT_NUMBER);
					setErrorMessage(null);
					CubicUnitRunnerPlugin.getDefault().
					getDialogSettings().put(CUBIC_UNIT_RUNNER_PORT_NUMBER,port);
				}catch(NumberFormatException ex){
					setErrorMessage("Please enter a number (" + portText.getText() + 
							" is not a number)");
					setPageComplete(false);
				}
				
			}
		});
		
		content.setLayout(gridLayout);
		setMessage(PLEASE_ENTER_A_PORT_NUMBER);
		setPageComplete(true);
		
		setControl(content);
	}
	
	public int getPort(){
		return port;
	}
	
	public BrowserType getBrowserType(){
		
		return browserType;
	}

	/**
	 * This method initializes browserCombo	
	 * @param content 
	 *
	 */
	private void createBrowserCombo(Composite content) {
		browserCombo = new Combo(content, SWT.NONE | SWT.READ_ONLY);
		browserCombo.add("FireFox");
		browserCombo.add("Internet Explorer");
		browserCombo.add("Opera");
		browserCombo.add("Safari");
	
		browserCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				switch(browserCombo.getSelectionIndex()){
				case 0:
					browserType = BrowserType.FIREFOX;
					break;
				case 1:
					browserType = BrowserType.INTERNET_EXPLORER;
					break;
				case 2:
					browserType = BrowserType.OPERA;
					break;
				case 3:
					browserType = BrowserType.SAFARI;
					break;
				}
				CubicUnitRunnerPlugin.getDefault().
					getDialogSettings().put(CUBIC_UNIT_RUNNER_BROWSER_TYPE,
							browserCombo.getSelectionIndex());
			}
		});
		int storedBrowserType = 0;
		try{
			storedBrowserType = CubicUnitRunnerPlugin.getDefault().
				getDialogSettings().getInt(CUBIC_UNIT_RUNNER_BROWSER_TYPE);
			if(storedBrowserType < 0 || storedBrowserType > 3)
				storedBrowserType = 0;
		}catch(NumberFormatException e){
		}
		browserCombo.select(storedBrowserType);
	}
}
