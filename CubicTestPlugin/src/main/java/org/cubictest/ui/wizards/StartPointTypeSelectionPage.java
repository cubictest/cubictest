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
	public boolean urlStartPointSelected = false;
	
	protected StartPointTypeSelectionPage() {
		super(NAME);
		setPageComplete(true);
		
		setTitle("Choose startpointtype for the new Test");
		setDescription("Choose startpointtype for the new Test");
	}

	public IWizardPage getNextPage() {
		if(urlStartPointSelected) {
			return getWizard().getPage(NewUrlStartPointPage.NAME);
		}
		else {
			IWizardPage page = getWizard().getPage(ExtentionStartPointSelectorPage.NAME);
			((ExtentionStartPointSelectorPage) page).setPageShown(true);
			return page;
		}

	}
	
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2,false);
		extentionStartPointRadio = new Button(container, SWT.RADIO);
		extentionStartPointRadio.addSelectionListener(this);
		extentionStartPointLabel = new Label(container, SWT.LEFT);
		extentionStartPointLabel.setText("Extension start point - Start from an extension point in another test");
		extentionStartPointLabel.addMouseListener(this);
		urlStartPointRadio = new Button(container, SWT.RADIO);
		urlStartPointRadio.addSelectionListener(this);
		urlStartPointLabel = new Label(container, SWT.LEFT);
		urlStartPointLabel.setText("URL start point - Start from a specific URL");
		urlStartPointLabel.addMouseListener(this);
		setSelected();
		container.setLayout(gridLayout);
		setControl(container);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		urlStartPointSelected = urlStartPointRadio.equals(e.getSource());
		setSelected();
	}

	public void widgetSelected(SelectionEvent e) {
		urlStartPointSelected = urlStartPointRadio.equals(e.getSource());
		setSelected();
	}	
	
	private void setSelected(){
		urlStartPointRadio.setSelection(urlStartPointSelected);
		extentionStartPointRadio.setSelection(!urlStartPointSelected);
	}

	public void mouseDoubleClick(MouseEvent e) {
		mouseDown(e);
	}

	public void mouseDown(MouseEvent e) {
		urlStartPointSelected = urlStartPointLabel.equals(e.getSource());
		setSelected();
	}

	public void mouseUp(MouseEvent e) {
	}
}
