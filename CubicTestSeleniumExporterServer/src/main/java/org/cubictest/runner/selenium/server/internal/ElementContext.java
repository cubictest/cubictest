package org.cubictest.runner.selenium.server.internal;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.selenium.custom.IElementContext;


public class ElementContext implements IElementContext {

	private Map<String, Object> map = new HashMap<String, Object>();
	
	@Override
	public Object get(String key) {
		return map.get(key);
	}

	@Override
	public void put(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public Object remove(String key) {
		return map.remove(key);
	}

}
