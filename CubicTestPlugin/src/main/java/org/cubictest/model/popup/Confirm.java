package org.cubictest.model.popup;

import org.cubictest.model.Text;


public class Confirm extends JavaScriptPopup{

	public Confirm() {
		elements.add(new Text());
		elements.add(new OKButton());
		elements.add(new CancelButton());
	}

	@Override
	public String getType() {
		return "Confirm";
	}
}
