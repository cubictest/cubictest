/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

/**
 * Interface for parameterization observers.
 * 
 * @author SK Skytteren
 *
 */
public interface SationObserver{

	public void setUseParam(boolean useParam);
	public boolean useParam();
	
	public void setUseI18n(boolean useI18n);
	public boolean useI18n();
	
	public void setValue(String value);
	
	public String getParamKey();
	
	public String getI18nKey();

	public String getValue();
}