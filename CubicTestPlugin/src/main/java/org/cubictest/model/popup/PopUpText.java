package org.cubictest.model.popup;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.IdentifierType;
import org.cubictest.model.Text;

public class PopUpText extends Text implements IPopUpElement{
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {
		ArrayList<IdentifierType> result = new ArrayList<IdentifierType>();
		result.add(IdentifierType.LABEL);
		return result;
	}
}
