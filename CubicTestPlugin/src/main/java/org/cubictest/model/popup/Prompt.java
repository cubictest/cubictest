package org.cubictest.model.popup;

import org.cubictest.model.Text;
import org.cubictest.model.formElement.TextField;


public class Prompt extends JavaScriptPopup{
	
	public Prompt() {
		elements.add(new Text());
		elements.add(new TextField());
		elements.add(new OKButton());
		elements.add(new CancelButton());
	}

	@Override
	public String getType() {
		return "Prompt";
	}
	
}
