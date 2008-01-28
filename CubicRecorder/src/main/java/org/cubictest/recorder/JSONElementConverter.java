/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.regexp.Regexp;
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
import org.cubictest.recorder.utils.TextUtil;
import org.json.JSONObject;

public class JSONElementConverter {
	private HashMap<String, PageElement> pageElements = new HashMap<String, PageElement>();
	private final String baseUrl;
	
	
	public JSONElementConverter(String baseUrl) {
		this.baseUrl = baseUrl;
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
				} else if(type.equalsIgnoreCase("image")) {
					pe = new Button();
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
				//override default "must" typically used for direct edit:
				initializeEmptyIdentifier(pe, idType);
			
				String key = null;
				String value;
				
				// Identifiers are in prioritized order! Only record one of them (more robust)
				switch (idType){
					case LABEL:
						if(pe instanceof Button) {
							key = "value";
							value = getString(properties, key);
							checkAndSetIdentifier(pe, idType, value, true);
						}
						else if (pe instanceof Link || pe instanceof Title || pe instanceof Option) {
							key = "innerHTML";
							value = getString(properties, key);
							checkAndSetIdentifier(pe, idType, value, true);
						}
						else {
							//TODO: Get html <label> tag 
						}
						break;
					case ID:
						key = "id";
						value = getString(properties, key);
						checkAndSetIdentifier(pe, idType, value, false);
						break;
					case SRC:
						key = "src";
						value = makeRelativeUrl(getString(properties, key));
						checkAndSetIdentifier(pe, idType, value, false);
						break;
					case NAME:
						key = "name";
						value = getString(properties, key);
						checkAndSetIdentifier(pe, idType, value, false);
						break;
					case HREF:
						key = "href";
						value = makeRelativeUrl(getString(properties, key));
						checkAndSetIdentifier(pe, idType, value, false);
						break;
					case TITLE:
						key = "title";
						value = getString(properties, key);
						checkAndSetIdentifier(pe, idType, value, true);
						break;
					case ALT:
						key = "alt";
						value = getString(properties, key);
						checkAndSetIdentifier(pe, idType, value, true);
						break;
					case VALUE:
						//TODO: Get value present when page loaded.
//						if(pe instanceof AbstractTextInput)
//							key = "value";
						break;
					case CHECKED:
						//TODO: Handle checked
//						key = "checked";
						break;
					case MULTISELECT:
						//TODO: Handle Multiselect
//						key = "multiple";
						break;
				} //end switch (idType)

			} //end for (idTypes)
			
			pageElements.put(getString(properties, "cubicId"), pe);
			return pe;
			
		} catch(NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Sets the identifier and returns true if it has a value.
	 * @param isShowInEditor 
	 */
	private void checkAndSetIdentifier(PageElement pe, IdentifierType idType, String value, boolean isShowInEditor) {
		if (StringUtils.isNotBlank(value)) {
			Identifier identifier = pe.getIdentifier(idType);
			value = TextUtil.normalize(value);
			value = StringEscapeUtils.unescapeHtml(value);
			identifier.setValue(value);
			if (pe.getMainIdentifier() == null) {
				identifier.setProbability(100);
				pe.setDirectEditIdentifier(identifier);
			}
			
			if (isShowInEditor) {
				pe.setDirectEditIdentifier(identifier);
			}
		}
	}

	private void initializeEmptyIdentifier(PageElement pe, IdentifierType idType) {
		Identifier identifier = pe.getIdentifier(idType);
		identifier.setValue("");
		identifier.setProbability(0);
	}

	public PageElement getPageElement(String cubicId) {
		return pageElements.get(cubicId);
	}
	
	private String makeRelativeUrl(String url) {
		return url.replaceAll("^" + Pattern.quote(baseUrl), "");
	}
}
