package org.cubictest.exporters.watir.ui;

import org.cubictest.model.customstep.data.CustomTestStepData;
import org.cubictest.model.customstep.data.CustomTestStepDataEvent;
import org.cubictest.model.customstep.data.ICustomTestStepDataListener;
import org.cubictest.ui.customstep.section.CustomStepSection;
import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

public class WatirCustomStepSection extends CustomStepSection implements ICustomTestStepDataListener{

	private IProject project;
	private CustomTestStepData data;
	private Link newRbFileLink;
	private Text classText;
	private Button browserRbFileButton;

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(ColorConstants.white);
		
		FormLayout formLayout = new FormLayout();
		composite.setLayout(formLayout);
		
		newRbFileLink = new Link(composite, SWT.PUSH);
		newRbFileLink.setText("<A>CubicTest Watir extension*: </A>");
		newRbFileLink.setBackground(ColorConstants.white);
		newRbFileLink.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				//TODO: implement something smart
			}
		});
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0,0);
		layoutData.width = STANDARD_LABEL_WIDTH;
		newRbFileLink.setLayoutData(layoutData);
		
		classText = new Text(composite,SWT.BORDER);
		classText.setBackground(ColorConstants.white);
		classText.setText(data.getDisplayText());
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(newRbFileLink);
		layoutData.width = STANDARD_LABEL_WIDTH * 2;
		classText.setLayoutData(layoutData);
		
		browserRbFileButton = new Button(composite, SWT.PUSH);
		browserRbFileButton.setText("Browse...");
		browserRbFileButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				//TODO: implement something smart
			}
		});
		layoutData = new FormData();
		layoutData.left = new FormAttachment(classText,5);
		browserRbFileButton.setLayoutData(layoutData);
	}

	@Override
	public String getDataKey(){
		return "org.cubictest.watirexporter";
	}

	@Override
	public void setData(CustomTestStepData data) {
		this.data = data;
		data.addChangeListener(this);
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public void handleEvent(CustomTestStepDataEvent event) {
		classText.setText(data.getDisplayText());
	}

}
