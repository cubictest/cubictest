package org.cubictest.selenium.custom;

public interface IElementContext{
	public void put(String key, Object value);
	public Object get(String key);
	public Object remove(String key);
}
