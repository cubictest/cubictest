/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.util;


/**
 * Enums for supported browsers.
 * 
 * @author Christian Schwarz
 */
public enum Browser {

	FIREFOX("*firefox"),
	IE("*iexplore"),
	OPERA("*opera");
	
	private String id;
	
	private Browser(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
