package org.cubictest.exporters.selenium;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.selenium.custom.IElementContext;


public class ElementContext implements IElementContext {

	private Map<String, Object> map = new HashMap<String, Object>();
	
	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public Object remove(String key) {
		return map.remove(key);
	}

}
