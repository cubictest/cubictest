/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.i18n;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cubictest.model.PageElement;
import org.cubictest.model.SationObserver;
import org.cubictest.ui.i18n.I18nMessage;


public class AllLanguages {
	private List<Language> languages;
	private List<SationObserver> observers;
	private Language currentLanguage;
		
	public AllLanguages() {
		languages = new ArrayList<Language>();
		observers = new ArrayList<SationObserver>();
	}
	
	public List<Language> getLanguages(){
		return languages;
	}
	
	public void addLanguage(Language language){
		//TODO legg til kompliteringskode
		languages.add(language);
	}

	public Set<String> getAllKeys() {
		Set<String> keys = new HashSet<String>();
		for(Language lang : languages)
			keys.addAll(lang.keySet());
		for(Language lang : languages)
			lang.keySet().addAll(keys);
		return keys;
	}

	public I18nMessage createMessage() {
		String key = "";
		for(Language lang : languages)
			lang.keySet().add(key);
		return new I18nMessage(this, key);
	}

	public Language createLanguage() {
		Language lang = new Language();
		lang.keySet().addAll(getAllKeys());
		return lang;
	}
	
	public void addObserver(SationObserver observer){
		observers.add(observer);
	}

	public Language getCurrentLanguage() {
		return currentLanguage;
	}
	public void setCurrentLanguage(Language currentLanguage) {
		this.currentLanguage = currentLanguage;
		updateObservers();
	}

	public void removeObserver(PageElement pe) {
		observers.remove(pe);
	}

	public void updateObservers() {
		if (currentLanguage == null)
			currentLanguage = getLanguages().get(0);
		
		for(SationObserver observer : observers){
			String key = observer.getI18nKey();
			if(observer.useI18n() && observer.useParam()){
				key = observer.getValue();
			}
			observer.setValue(currentLanguage.get(key));
		}
	}
}
