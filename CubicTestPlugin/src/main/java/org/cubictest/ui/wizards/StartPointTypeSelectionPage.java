/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StartPointTypeSelectionPage extends WizardPage implements SelectionListener, MouseListener {

	public static final String NAME = "startPointTypeSelection";
	public Button urlStartPointRadio = null;
	private Label urlStartPointLabel = null;
	public Button extentionStartPointRadio = null;
	private Label extentionStartPointLabel = null;
	public Button subTestStartPointRadio = null;
	private Label subTestStartPointLabel = null;
	private boolean urlStartPointSelected = false;
	private boolean subTestStartPointSelected = false;
	private final boolean testHasExtensionPoints;
	
	protected StartPointTypeSelectionPage(boolean testHasExtensionPoints) {
		super(NAME);
		this.testHasExtensionPoints = testHasExtensionPoints;
		setPageComplete(true);
		
		setTitle("Choose startpoint type for the new Test");
		setDescription("Choose startpoint type for the new Test");
	}

	public IWizardPage getNextPage() {
		ExtentionStartPointSelectorPage exStartPointPage = (ExtentionStartPointSelectorPage) getWizard().getPage(ExtentionStartPointSelectorPage.NAME);
		
		if(urlStartPointSelected) {
			exStartPointPage.setPageWasNext(false);
			return getWizard().getPage(NewUrlStartPointPage.NAME);
		}
		else if(subTestStartPointSelected) {
			exStartPointPage.setPageWasNext(false);
			return null;
		}
		else {
			exStartPointPage.setPageWasNext(true);
			return exStartPointPage;
		}

	}
	
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2,false);

		createUrlStartPointOption(container);
		createExtensionStartPointOption(container);
		createSubTestStartPointOption(container);
		
		if (!testHasExtensionPoints) {
			urlStartPointSelected = true;
		}
		setSelected();
		container.setLayout(gridLayout);
		setControl(container);
	}

	private void createSubTestStartPointOption(Composite container) {
		subTestStartPointRadio = new Button(container, SWT.RADIO);
		subTestStartPointRadio.addSelectionListener(this);
		subTestStartPointLabel = new Label(container, SWT.LEFT);
		subTestStartPointLabel.setText("Sub test start point - Unspecified start. Test can only run as sub test of another test");
		subTestStartPointLabel.addMouseListener(this);
	}

	private void createUrlStartPointOption(Composite container) {
		urlStartPointRadio = new Button(container, SWT.RADIO);
		urlStartPointRadio.addSelectionListener(this);
		urlStartPointLabel = new Label(container, SWT.LEFT);
		urlStartPointLabel.setText("URL start point - Start from a specific URL");
		urlStartPointLabel.addMouseListener(this);
	}

	private void createExtensionStartPointOption(Composite container) {
		extentionStartPointRadio = new Button(container, SWT.RADIO);
		extentionStartPointRadio.addSelectionListener(this);
		extentionStartPointLabel = new Label(container, SWT.LEFT);
		extentionStartPointLabel.setText("Extension start point - Start from an extension point in another test");
		extentionStartPointLabel.addMouseListener(this);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		urlStartPointSelected = urlStartPointRadio.equals(e.getSource());
		subTestStartPointSelected = subTestStartPointRadio.equals(e.getSource());
		setSelected();
	}

	public void widgetSelected(SelectionEvent e) {
		urlStartPointSelected = urlStartPointRadio.equals(e.getSource());
		subTestStartPointSelected = subTestStartPointRadio.equals(e.getSource());
		setSelected();
	}	
	
	private void setSelected(){
		urlStartPointRadio.setSelection(urlStartPointSelected);
		subTestStartPointRadio.setSelection(subTestStartPointSelected);
		extentionStartPointRadio.setSelection(!urlStartPointSelected && !subTestStartPointSelected);
		setPageComplete(true);
	}

	public void mouseDoubleClick(MouseEvent e) {
		mouseDown(e);
	}

	public void mouseDown(MouseEvent e) {
		urlStartPointSelected = urlStartPointLabel.equals(e.getSource());
		subTestStartPointSelected = subTestStartPointLabel.equals(e.getSource());
		setSelected();
	}

	public void mouseUp(MouseEvent e) {
	}

	public boolean isUrlStartPointSelected() {
		return urlStartPointSelected;
	}

	public boolean isSubTestStartPointSelected() {
		return subTestStartPointSelected;
	}

	public boolean isExtensionStartPointSelected() {
		return !subTestStartPointSelected && !urlStartPointSelected;
	}

	public void setUrlStartPointSelected(boolean urlStartPointSelected) {
		this.urlStartPointSelected = urlStartPointSelected;
	}
}
