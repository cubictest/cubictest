/*
 * Created on 4.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import static org.cubicunit.InputType.BUTTON;
import static org.cubicunit.InputType.CHECKBOX;
import static org.cubicunit.InputType.PASSWORD;
import static org.cubicunit.InputType.RADIO;
import static org.cubicunit.InputType.SELECT;
import static org.cubicunit.InputType.TEXT;
import static org.cubicunit.InputType.TEXTAREA;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.model.FormElement;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubicunit.Container;
import org.cubicunit.Document;
import org.cubicunit.Element;
import org.cubicunit.IdentifierType;
import org.cubicunit.Input;
import org.cubicunit.InputType;


public class ElementConverter implements IPageElementConverter<Holder> {

	public void handlePageElement(Holder holder,PageElement pe) {
		Container container = holder.getContainer();
		Element element = null;
		String text = pe.getText();
		boolean not = pe.isNot();
		try{
			if (pe instanceof Text) {
				if (not)
					container.assertTextNotPresent(text);
				else
					container.assertTextPresent(text);
			}else if (pe instanceof Link) {
				if(not)
					container.assertLinkNotPresent(text);
				else
					element = container.assertLinkPresent(text);
			}else if (pe instanceof Image) {
				if(not)
					container.assertImageNotPresent(text);
				else
					element = container.assertImagePresent(text);
			}else if (pe instanceof Title){
				Document doc = holder.getDocument();
				if(not)
					doc.assertNotTitle(text);
				else 
					doc.assertTitle(text);
			}else if (pe instanceof FormElement){
				FormElement formElement = (FormElement)pe;
				IdentifierType it = IdentifierType.valueOf(formElement.
						getDirectEditIdentifier().getType().toString().toUpperCase()); 			
				InputType type;
				if(pe instanceof TextArea)
					type = TEXTAREA;
				else if (pe instanceof Button)
					type = BUTTON;
				else if (pe instanceof Select)
					type = SELECT;
				else if (pe instanceof Checkbox)
					type = CHECKBOX;
				else if (pe instanceof RadioButton)
					type = RADIO;
				else if (pe instanceof Password)
					type = PASSWORD;
				else if (pe instanceof TextField)
					type = TEXT;
				else 
					type = TEXT;
				if(not)
					container.assertInputNotPresent(type, it, text);
				else
					element = (Input)container.assertInputPresent(type, it, text);
			}else if (pe instanceof IContext){
				throw new IllegalArgumentException();
			}else{
				System.err.println("Error could not convert: " + pe);
			}
			holder.put(pe,element);
			pe.setStatus(TestPartStatus.PASS);
		}catch(AssertionError e){
			pe.setStatus(TestPartStatus.FAIL);
			throw e;
		}catch(RuntimeException e){
			pe.setStatus(TestPartStatus.EXCEPTION);
			throw e;
		}
	}
}
