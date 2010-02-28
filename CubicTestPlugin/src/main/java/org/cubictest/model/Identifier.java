/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.CubicCloner;
import org.cubictest.common.utils.TextUtil;


/**
 * Identifier (e.g. label, ID, tooltip) that identifies a page element on a web page.
 * The identifier does not have to be unique on the page, as several identifiers is usually combined
 * to identify a page element on a page.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 */
public class Identifier implements Cloneable, SationObserver{

	public final static int MAX_PROBABILITY = 100;
	public final static int INDIFFERENT_PROBABILITY = 0;
	private String value = "";
	private int probability = INDIFFERENT_PROBABILITY;
	private IdentifierType type;
	private String actual;
	private String i18nKey;
	private String paramKey;
	public final static String VALUE = "value";
	public final static String TYPE = "type";
	public final static String PROBABILITY = "probability";
	public static final String MODERATOR = "moderator";
	public static final String ACTUAL = "actual";
	private static final String USE_I18N = "useI18n";
	private static final String I18N_KEY = "i18nKey";
	private static final String USE_PARAM = "userParam";
	private static final String PARAM_KEY = "paramKey";
	private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private boolean useI18n = false;
	private boolean useParam = false;
	private Moderator moderator = Moderator.EQUAL;
	
	public void setType(IdentifierType type){
		IdentifierType oldType = type;
		this.type = type;
		firePropertyChanged(TYPE, oldType, type);
	}

	public IdentifierType getType() {
		return type;
	}

	public void setValue(String value){
		String oldValue = this.value;
		this.value = TextUtil.normalizeWithPossibleSingleTrailingSpace(value);
		firePropertyChanged(VALUE, oldValue, this.value);
	}
	
	private void firePropertyChanged(String propertyName,Object oldValue, Object newValue){
		getListeners().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public String getValue(){
		return TextUtil.normalizeAndTrim(value);
	}

	public String getValueWithPossibleSingleTrailingSpace(){
		return TextUtil.normalizeWithPossibleSingleTrailingSpace(value);
	}

	public void setProbability(int probability){
		int oldProbability = this.probability;
		if(probability > MAX_PROBABILITY){
			this.probability = MAX_PROBABILITY;
		}else if(probability < -MAX_PROBABILITY){
			this.probability = -MAX_PROBABILITY;
		}else{
			this.probability = probability;
		}
		firePropertyChanged(PROBABILITY, oldProbability, probability);
	}
	public int getProbability(){
		return probability;
	}
	
	public void setActual(String actual) {
		String oldActual = actual;
		this.actual = actual;
		firePropertyChanged(ACTUAL, oldActual, actual);
	}
	
	public String getActual() {
		return actual;
	}
	
	@Override
	public Identifier clone() throws CloneNotSupportedException {
		return (Identifier) CubicCloner.deepCopy(this);

	}

	public String getI18nKey() {
		return i18nKey;
	}
	
	public String getParamKey() {
		return paramKey;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getListeners().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getListeners().removePropertyChangeListener(listener);
	}
	
	private PropertyChangeSupport getListeners() {
		if (listeners == null)
			listeners = new PropertyChangeSupport(this);
		return listeners;
	}

	public void setUseI18n(boolean useI18n) {
		boolean oldUseI18n = this.useI18n;
		this.useI18n = useI18n;
		firePropertyChanged(USE_I18N, oldUseI18n, useI18n);
	}

	public void setUseParam(boolean useParam) {
		boolean oldUseParam = this.useParam;
		this.useParam = useParam;
		firePropertyChanged(USE_PARAM, oldUseParam, useParam);
	}

	public boolean useI18n() {
		return useI18n;
	}

	public boolean useParam() {
		return useParam;
	}

	public void setI18nKey(String key) {
		String oldKey = i18nKey;
		i18nKey = key;
		firePropertyChanged(I18N_KEY, oldKey, i18nKey);
	}
	
	public void setParamKey(String paramKey) {
		String oldParamKey = paramKey;
		this.paramKey = paramKey;
		firePropertyChanged(PARAM_KEY, oldParamKey, paramKey);
	}
	@Override
	public String toString() {
		return type + " " + getModerator().getDisplayText() + " \"" + value + "\" (probability = " + probability + ")";
	}
	
	public boolean isNotIndifferent() {
		return probability != 0;
	}
	
	public boolean isIndifferent() {
		return probability == INDIFFERENT_PROBABILITY;
	}
	
	public boolean isMaxProbability() {
		return probability == MAX_PROBABILITY;
	}
	
	public boolean isNotBlank() {
		return StringUtils.isNotBlank(getValue());
	}

	public Moderator getModerator() {
		if (moderator == null)
			moderator = Moderator.EQUAL;
		return moderator;
	}

	public void setModerator(Moderator moderator) {
		Moderator oldModerator = this.moderator;
		this.moderator = moderator;
		firePropertyChanged(MODERATOR, oldModerator, moderator);
	}
}
