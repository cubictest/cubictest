package org.cubictest.recorder;

import java.text.ParseException;

import org.cubictest.model.ActionType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;

import com.metaparadigm.jsonrpc.JSONSerializer;

public class JSONRecorder {
	private final IRecorder recorder;
	private JSONSerializer serializer;
	private final JSONElementConverter converter;

	public JSONRecorder(IRecorder recorder, JSONElementConverter converter) {
		this.recorder = recorder;
		this.converter = converter;
		
		serializer = new JSONSerializer();
		try {
			serializer.registerDefaultSerializers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean assertPresent(String json) {
		try {
			PageElement pe = converter.createElementFromJson(json);
			if(pe != null) {
				recorder.addPageElement(pe);
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean assertNotPresent(String json) {
		try {
			PageElement pe = converter.createElementFromJson(json);
			pe.setNot(true);
			recorder.addPageElement(pe);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addAction(String actionType, String jsonElement) {
		this.addAction(actionType, jsonElement, "");
	}

	public void addAction(String actionType, String jsonElement, String value) {
		try {
			PageElement pe = converter.createElementFromJson(jsonElement);
			if(pe != null) {
				UserInteraction action = new UserInteraction(pe, ActionType.getActionType(actionType), value);
				recorder.addPageElement(pe);
				recorder.addUserInput(action);
			} else {
				System.out.println("Action ignored");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void setStateTitle(String title) {
		recorder.setStateTitle(title);
	}
}