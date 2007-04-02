package org.cubictest.model.popup;

import org.cubictest.model.Text;


public class Alert extends JavaScriptPopup{

	public Alert() {
		elements.add(new Text());
		elements.add(new OKButton());
	}

	@Override
	public String getType() {
		return "Alert";
	}
	
}
