package org.cubictest.recorder;

import java.text.ParseException;
import java.util.NoSuchElementException;

import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.formElement.AbstractTextInput;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.TextField;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONObject;

import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.UnmarshallException;

public class JSONRecorder {
	private final IRecorder recorder;
	private JSONSerializer serializer;

	public JSONRecorder(IRecorder recorder) {
		this.recorder = recorder;
		
		serializer = new JSONSerializer();
		try {
			serializer.registerDefaultSerializers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean assertPresent(String elementJsonData) {
		try {
			JSONObject element = new JSONObject(elementJsonData);
			PageElement pe = createElementFromJson(element);
			recorder.addPageElement(pe);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean assertNotPresent(String elementJsonData) {
		try {
			JSONObject element = new JSONObject(elementJsonData);
			PageElement pe = createElementFromJson(element);
			pe.setNot(true);
			recorder.addPageElement(pe);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addAction(String actionJsonData) {
		
	}
	
	/**
	 * Parses a JSON structure and returns a new page element
	 * 
	 * Example JSON structure:
	 * {
	 *   tagName: "INPUT",
	 *   label: "Text Input:", 
	 *   attributes: {
	 *     type: "text",
	 *     value: "blablabla",
	 *     class: "someclass",
	 *     id: "someid"
	 *   }
	 * }
	 * 
	 * @param element
	 * @return
	 */
	private PageElement createElementFromJson(JSONObject element) {
		JSONObject attributes = element.getJSONObject("attributes");
		PageElement pe = null;
		if(element.getString("tagName").equals("INPUT")) {
			String type = attributes.getString("type");
			if(type.equals("text")) {
				pe = new TextField();		
			} else if(type.equals("radio")) {
				pe = new RadioButton();
			} else if(type.equals("checkbox")) {
				pe = new Checkbox();
			}
		
			AbstractTextInput txt = (AbstractTextInput) pe;

			try {
				pe.setText(element.getString("label"));
				pe.setIdentifierType(IdentifierType.LABEL);
			} catch(NoSuchElementException eLabel) {
				try {
					pe.setText(attributes.getString("id"));
					pe.setIdentifierType(IdentifierType.ID);				
				} catch(NoSuchElementException eId) {
					try {
						pe.setText(attributes.getString("name"));
						pe.setIdentifierType(IdentifierType.NAME);
					} catch(NoSuchElementException eName) {
						
					}
				}
			}
		}
		
		if(pe != null) {
		}
		
		return pe;
	}
}