/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

public interface SationObserver{

	public enum SationType{
		NONE,
		PARAMETERISATION,
		INTERNATIONALISATION,
		BOTH;
	}

	public void setSationType(SationType type);
	
	public SationType getSationType();
	
	public void setText(String text);
	
	public String getKey();

	public String getText();
}