package org.cubictest.model.customstep;

public class CustomTestStepEvent {

	private final String key;
	private final Object oldValue;
	private final Object newValue;

	public CustomTestStepEvent(String key, Object oldValue, Object newValue) {
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String getKey() {
		return key;
	}
	
	public Object getOldValue() {
		return oldValue;
	}
	
	public Object getNewValue() {
		return newValue;
	}

}
