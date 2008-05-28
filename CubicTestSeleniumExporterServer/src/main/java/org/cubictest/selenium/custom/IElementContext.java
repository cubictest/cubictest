package org.cubictest.selenium.custom;

/**
 * A container for holding variables between Custom Test Steps.
 * 
 * @author SK Skytteren
 *
 */
public interface IElementContext{
	/**
	 * Used for setting a key value pair
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value);
	/**
	 * To get the stored value
	 * @param key 
	 * @return
	 */
	public Object get(String key);
	/**
	 * Removed the value stored for the given key.
	 * @param key
	 * @return
	 */
	public Object remove(String key);
}
