/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Title;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.json.JSONObject;

public class JSONElementConverter {
	private HashMap<String, PageElement> pageElements = new HashMap<String, PageElement>();

	public JSONElementConverter() {
		
	}
	
	private String getString(JSONObject object, String property) {
		try {
			return object.getString(property);
		} catch(NoSuchElementException e) {
			return null;
		}
	}
	
	private JSONObject getJSONObject(JSONObject object, String property) {
		try {
			return object.getJSONObject(property);
		} catch(NoSuchElementException e) {
			return null;
		}
	}
	
	/**
	 * Parses a JSON structure and returns a new page element
	 * 
	 * Example JSON structure:
	 * {
	 *   label: "Text Input:", 
	 *   properties: {
	 *     tagName: "INPUT",
	 *     type: "text",
	 *     value: "blablabla",
	 *     class: "someclass",
	 *     id: "someid"
	 *   }
	 * }
	 * 
	 * @param element
	 * @return
	 * @throws ParseException
	 */
	public PageElement createElementFromJson(String json) throws ParseException {
		System.out.println(json);
		return this.createElementFromJson(new JSONObject(json));
	}
	
	public PageElement createElementFromJson(JSONObject element) {
		try {
			JSONObject properties = getJSONObject(element, "properties");
			if(pageElements.get(getString(properties, "cubicId")) != null) {
				return pageElements.get(getString(properties, "cubicId")); 
			}
			
			PageElement pe = null;
			
			//creating correct page element instance:
			if(getString(properties, "tagName").equalsIgnoreCase("INPUT")) {
				String type = properties.getString("type");
				if(type.equalsIgnoreCase("text")) {
					pe = new TextField();		
				} else if(type.equalsIgnoreCase("password")) {
					pe = new Password();
				} else if(type.equalsIgnoreCase("submit")) {
					pe = new Button();
				} else if(type.equalsIgnoreCase("button")) {
					pe = new Button();
				} else if(type.equalsIgnoreCase("radio")) {
					pe = new RadioButton();
				} else if(type.equalsIgnoreCase("checkbox")) {
					pe = new Checkbox();
				}
			} else if(getString(properties, "tagName").equalsIgnoreCase("TEXTAREA")) {
				pe = new TextArea();
			} else if(getString(properties, "tagName").equalsIgnoreCase("SELECT")) {
				pe = new Select();
			} else if(getString(properties, "tagName").equalsIgnoreCase("OPTION")) {
				pe = new Option();
			} else if(getString(properties, "tagName").equalsIgnoreCase("BUTTON")) {
				pe = new Button();
			} else if(getString(properties, "tagName").equalsIgnoreCase("IMG")) {
				pe = new Image();
			} else if(getString(properties, "tagName").equalsIgnoreCase("A")) {
				pe = new Link();
			} else if(getString(properties, "tagName").equalsIgnoreCase("TITLE")) {
				pe = new Title();
			} else if(getString(properties, "tagName").equalsIgnoreCase("DIV") || getString(properties, "tagName").equalsIgnoreCase("TABLE")) {
				pe = new SimpleContext();
			}
			
			if(pe == null) {
				return null;
			}
			
			//looping over the created page element's ID types and setting all applicable values:
			
			for(IdentifierType idType : pe.getIdentifierTypes()){
				String key = null;
				
				switch (idType){
					case CHECKED:
						//TODO: Handle checked
//						key = "checked";
						break;
					case ID:
						key = "id";
						break;
					case SRC:
						//TODO: Handle relative/absolute URLs. Selenium always gets absolute, even if page uses relative URL.
//						key = "src";
						break;
					case LABEL:
						if(pe instanceof Button)
							key = "value";
						else if (pe instanceof Link || pe instanceof Title || pe instanceof Option)
							key = "innerHTML";
						else
							//TODO: Get html <label> tag 
							key = null;
						break;
					case NAME:
						key = "name";
						break;
					case HREF:
						//TODO: Handle relative/absolute URLs. Selenium always gets absolute, even if page uses relative URL.
//						key = "href";
						break;
					case TITLE:
						key = "title";
						break;
					case VALUE:
						//TODO: Get value present when page loaded.
//						if(pe instanceof AbstractTextInput)
//							key = "value";
						break;
					case MULTISELECT:
						//TODO: Handle Multiselect
//						key = "multiple";
						break;
					case INDEX:
						key = "index";
						break;

				} //end switch (idType)

				if (key == null) {
					Identifier identifier = pe.getIdentifier(idType);
					identifier.setValue("");
					identifier.setProbability(0);
				}
				else {
					String value = getString(properties, key);
					if (StringUtils.isNotBlank(value)) {
						Identifier identifier = pe.getIdentifier(idType);
						value = value.trim();
						value = StringEscapeUtils.unescapeHtml(value);
						identifier.setValue(value);
						identifier.setProbability(100);
					}
				}

			} //end for (idTypes)
			
			pageElements.put(getString(properties, "cubicId"), pe);
			pe.setDirectEditIdentifier(pe.getMainIdentifier());
			
			return pe;
		} catch(NoSuchElementException e) {
			return null;
		}
	}

	public PageElement getPageElement(String cubicId) {
		return pageElements.get(cubicId);
	}
}
