/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.wizards;

import java.util.List;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class WizardSelectExtensionPointPage extends WizardPage {
	
	private SubTest subTest;
	private Listener listener;

	protected WizardSelectExtensionPointPage(SubTest subTest, Test test, Listener listener) {
		super("selectExtensionPointPage");
		this.subTest = subTest;
		this.listener = listener;
	}
	
	public List<ExtensionPoint> getExtensionPoints() {
		return subTest.getTest().getAllExtensionPoints();
	}
	
	public void createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 4;
		content.setLayout(layout);
		
		Label fill = new Label(content, SWT.NULL);
		fill.setText("Select Extension Point");
		Combo options;
		
		options = new Combo(content, SWT.READ_ONLY);
		options.addListener(SWT.Selection, listener);
		
		for (ExtensionPoint ep : getExtensionPoints()) {
			options.add(ep.getName());
		}
		setControl(content);
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

//	@Override
//	public boolean isPageComplete() {
//		if(getSelectedExtensionPoint() != null) {
//			return true;
//		}
	// return false;
	//	}	
}
