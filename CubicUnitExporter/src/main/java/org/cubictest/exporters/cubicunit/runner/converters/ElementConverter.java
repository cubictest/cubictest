/*
 * Created on 4.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
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
import org.cubicunit.ElementTypes;
import org.cubicunit.internal.types.ButtonTypeImpl;
import org.cubicunit.types.ElementType;
import org.cubicunit.types.ImageType;
import org.cubicunit.types.InputType;
import org.cubicunit.types.LinkType;
import org.cubicunit.types.SelectMenuType;
import org.cubicunit.types.SelectType;
import org.cubicunit.types.TextInputType;


public class ElementConverter implements IPageElementConverter<Holder> {

	public void handlePageElement(Holder holder,PageElement pe) {
		Container container = holder.getContainer();
		boolean not = pe.isNot();
		try{
			
			if (pe instanceof Text) {
				String text = pe.getIdentifier(IdentifierType.LABEL).getValue();
				if (not != container.containsText(text))
					pe.setStatus(TestPartStatus.PASS);
				else 
					pe.setStatus(TestPartStatus.FAIL);
			}else if (pe instanceof Title){
				String text = pe.getIdentifier(IdentifierType.LABEL).getValue();
				Document doc = holder.getDocument();
				if (not != doc.getTitle().equals(text))
					pe.setStatus(TestPartStatus.PASS);
				else 
					pe.setStatus(TestPartStatus.FAIL);
			}else{
				ElementType<?> elementType = null;
				if (pe instanceof Link) {
					elementType = ElementTypes.LINK;
				}else if (pe instanceof Image) {
					elementType = ElementTypes.IMAGE;
				}else if (pe instanceof FormElement){
					if(pe instanceof TextArea)
						elementType = ElementTypes.TEXT_AREA;
					else if (pe instanceof Button)
						elementType = ElementTypes.BUTTON;
					else if (pe instanceof Select)
						elementType = ElementTypes.SELECT_MENU;
					else if (pe instanceof Checkbox)
						elementType = ElementTypes.CHECKBOX;
					else if (pe instanceof RadioButton)
						elementType = ElementTypes.RADIO_BUTTON;
					else if (pe instanceof Password)
						elementType = ElementTypes.PASSWORD_FIELD;
					else if (pe instanceof TextField)
						elementType = ElementTypes.TEXT_FIELD;
					else{
						elementType = ElementTypes.ELEMENT;
						ErrorHandler.logAndShowErrorDialog("Error could not convert: " + pe);
					}
				}else if (pe instanceof IContext){
					throw new IllegalArgumentException();
				}else{
					elementType = ElementTypes.ELEMENT;
					ErrorHandler.logAndShowErrorDialog("Error could not convert: " + pe);
				}
				for(Identifier id : pe.getIdentifiers()){
					switch (id.getType()) {
						case CHECKED:
							elementType = ((SelectType)elementType).
								checked(id.getProbability(), Boolean.getBoolean(id.getValue()));
							break;
						case HREF:
							elementType = ((LinkType)elementType).href(
									id.getProbability(), id.getValue());
							break;
						case ID:
							elementType = elementType.id(id.getProbability(), id.getValue());
							break;
						case INDEX:
							// TODO
							break;
						case LABEL:
							elementType = ((ButtonTypeImpl)elementType).
								label(id.getProbability(), id.getValue());
							break;
						case MULTISELECT:
							elementType = ((SelectMenuType)elementType).
								multiSelect(id.getProbability(), Boolean.getBoolean(id.getValue()));
							break;
						case NAME:
							elementType = ((InputType)elementType).
								name(id.getProbability(), id.getValue());
							break;
						case PATH:
							elementType = elementType.path(id.getProbability(), id.getValue());
							break;
						case SRC:
							elementType = ((ImageType)elementType).src(id.getProbability(), id.getValue());
							break;
						case TITLE:
							elementType = elementType.title(id.getProbability(), id.getValue());
							break;
						case VALUE:
							elementType = ((TextInputType)elementType).value
								(id.getProbability(), id.getValue());
							break;
					}
				}
				Element element = container.get(elementType);
				if(not)
					if(element == null)
						pe.setStatus(TestPartStatus.PASS);
					else
						pe.setStatus(TestPartStatus.FAIL);
				else{
					holder.put(pe,element);
					pe.setStatus(TestPartStatus.PASS);
				}
			}
		}catch(RuntimeException e){
			pe.setStatus(TestPartStatus.EXCEPTION);
			throw e;
		}
	}
}
