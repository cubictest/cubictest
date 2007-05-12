package org.cubictest.exporters.cubicunit.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;

import com.sun.org.apache.bcel.internal.util.BCELifier;

public class Slettes extends Composite {

	private Label browserLabel = null;
	private Combo browserCombo = null;

	public Slettes(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		browserLabel = new Label(this, SWT.NONE);
		browserLabel.setText("Label");
		this.setLayout(gridLayout);
		createBrowserCombo();
		setSize(new Point(300, 200));
	}

	/**
	 * This method initializes browserCombo	
	 *
	 */
	private void createBrowserCombo() {
		browserCombo = new Combo(this, SWT.NONE);
		browserCombo.add("FireFox");
		browserCombo.add("Internet Explorer");
		browserCombo.add("Opera");
		browserCombo.add("Safari");
	}

}
