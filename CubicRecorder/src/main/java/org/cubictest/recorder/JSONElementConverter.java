package org.cubictest.recorder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Title;
import org.cubictest.model.context.SimpleContext;
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
			
			if(getString(properties, "tagName").equals("DIV")) {
				pe.setMainIdentifierType(IdentifierType.ID);
				pe.setMainIdentifierValue(getString(properties, "id"));
			} else if(getString(properties, "tagName").equals("IMG")) {
				if(getString(properties, "id") != null && !getString(properties, "id").equals("")) {
					pe.setMainIdentifierType(IdentifierType.ID);
					pe.setMainIdentifierValue(getString(properties, "id"));
				} else {
					pe.setMainIdentifierType(IdentifierType.LABEL);
					pe.setMainIdentifierValue(getString(properties, "src"));			
				}
			} else if(getString(properties, "tagName").equals("BUTTON") || 
			  (getString(properties, "tagName").equals("INPUT") && 
			    (getString(properties, "type").equals("button") || getString(properties, "type").equals("submit")))) {
				pe.setMainIdentifierType(IdentifierType.LABEL);
				pe.setMainIdentifierValue(getString(properties, "value"));
			} else if(getString(properties, "tagName").equals("A")) {
				String text = getString(properties, "innerHTML").trim();
				pe.setMainIdentifierType(IdentifierType.LABEL);
				pe.setMainIdentifierValue(text);
			} else if(getString(properties, "tagName").equals("TITLE")) {
				pe.setMainIdentifierType(IdentifierType.TITLE);
				pe.setMainIdentifierValue(getString(properties, "innerHTML").trim());
			} else if(getString(element, "label") != null && !getString(element, "label").equals("")) {
				pe.setMainIdentifierType(IdentifierType.LABEL);
				pe.setMainIdentifierValue(getString(element, "label").trim());
			} else if(getString(properties, "id") != null && !getString(properties, "id").equals("")) {
				pe.setMainIdentifierType(IdentifierType.ID);				
				pe.setMainIdentifierValue(getString(properties, "id"));
			} else if(getString(properties, "name") != null && !getString(properties, "name").equals("")) {
				pe.setMainIdentifierType(IdentifierType.NAME);
				pe.setMainIdentifierValue(getString(properties, "name"));
			}
			
			pageElements.put(getString(properties, "cubicId"), pe);
			
			return pe;
		} catch(NoSuchElementException e) {
			return null;
		}
	}
}
