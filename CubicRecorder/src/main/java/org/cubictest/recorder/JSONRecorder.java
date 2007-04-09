package org.cubictest.recorder;

import java.text.ParseException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.ActionType;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
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
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	public boolean assertPresent(String json, String contextCubicId) {
		try {
			PageElement parent = converter.getPageElement(contextCubicId);
			PageElement pe = converter.createElementFromJson(json);
			if(pe != null) {
				recorder.addPageElement(pe, parent);
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		
		return false;
	}
	
	public boolean assertNotPresent(String json, String contextCubicId) {
		try {
			PageElement pe = converter.createElementFromJson(json);
			PageElement parent = converter.getPageElement(contextCubicId);
			pe.setNot(true);
			recorder.addPageElement(pe, parent);
			return true;
		} catch (ParseException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		return false;
	}
	
	public boolean assertTextPresent(String text, String contextCubicId) {
		PageElement pe = new Text();
		pe.setText(text);
		PageElement parent = converter.getPageElement(contextCubicId);
		recorder.addPageElement(pe,parent);
		return true;
	}
	
	public void addAction(String actionType, String jsonElement) {
		this.addAction(actionType, jsonElement, "");
	}

	public void addAction(String actionType, String jsonElement, String value) {
		try {
			PageElement pe = converter.createElementFromJson(jsonElement);
			if(pe != null) {
				UserInteraction action = new UserInteraction(pe, ActionType.getActionType(actionType), value);
				recorder.addUserInput(action);
			} else {
				System.out.println("Action ignored");
			}
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
	}
	
	public void setStateTitle(String title) {
		recorder.setStateTitle(title);
	}
}