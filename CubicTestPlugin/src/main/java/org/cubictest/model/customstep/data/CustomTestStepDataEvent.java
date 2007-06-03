package org.cubictest.model.customstep.data;

public class CustomTestStepDataEvent {
	
	private final EventType type;
	private final String oldText;
	private final String text;
	private CustomTestStepData data;

	public enum EventType{DISPLAY_TEXT, PATH};
	
	public CustomTestStepDataEvent(CustomTestStepData data, EventType type, String oldText, String text) {
		this.type = type;
		this.oldText = oldText;
		this.text = text;
		this.data = data;
	}

	public EventType getType() {
		return type;
	}
	
	public String getOldText() {
		return oldText;
	}
	
	public String getNewText() {
		return text;
	}
	
	public CustomTestStepData getData() {
		return data;
	}

}
