package org.cubictest.recorder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Title;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.AbstractTextInput;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
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

			if(getString(properties, "tagName").equals("INPUT")) {
				String type = properties.getString("type");
				if(type.equals("text")) {
					pe = new TextField();		
				} else if(type.equals("password")) {
					pe = new Password();
				} else if(type.equals("submit")) {
					pe = new Button();
				} else if(type.equals("button")) {
					pe = new Button();
				} else if(type.equals("radio")) {
					pe = new RadioButton();
				} else if(type.equals("checkbox")) {
					pe = new Checkbox();
				}
			} else if(getString(properties, "tagName").equals("TEXTAREA")) {
				pe = new TextArea();
			} else if(getString(properties, "tagName").equals("SELECT")) {
				pe = new Select();
			} else if(getString(properties, "tagName").equals("BUTTON")) {
				pe = new Button();
			} else if(getString(properties, "tagName").equals("IMG")) {
				pe = new Image();
			} else if(getString(properties, "tagName").equals("A")) {
				pe = new Link();
			} else if(getString(properties, "tagName").equals("TITLE")) {
				pe = new Title();
			} else if(getString(properties, "tagName").equals("DIV") || getString(properties, "tagName").equals("TABLE")) {
				pe = new SimpleContext();
			}
			
			if(pe == null) {
				return null;
			}
			
			for(IdentifierType type : pe.getIdentifierTypes()){
				String key = null;
				switch (type){
					case CHECKED:
						pe.getIdentifier(type).setValue("");
						break;
					case ID:
						key = "id";
						break;
					case SRC:
						key = "src";
						break;
					case LABEL:
						if(pe instanceof Button)
							key = "value";
						else if (pe instanceof Link || pe instanceof Title)
							key = "innerHTML";
						break;
					case NAME:
						key = "name";
						break;
					case HREF:
						key = "href";
						break;
					case TITLE:
						key = "title";
						break;
					case VALUE:
						if(pe instanceof AbstractTextInput)
							key = "value";
						break;
					case MULTISELECT:
						key = "multiselect";
						break;
					case INDEX:
						key = "index";
						break;
				}	
				if (pe != null){
					Identifier identifier = pe.getIdentifier(type);
					identifier.setValue(getString(properties, key));
					identifier.setProbability(100);
				}
			}
			pageElements.put(getString(properties, "cubicId"), pe);
			
			return pe;
		} catch(NoSuchElementException e) {
			return null;
		}
	}
}
