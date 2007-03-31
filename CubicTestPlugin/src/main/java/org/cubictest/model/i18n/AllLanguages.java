/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.i18n;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SationObserver;


public class AllLanguages extends PropertyAwareObject{
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
		languages.add(language);
		firePropertyChange(CHILD, null, language);
	}
	public void removeLanguage(Language language) {
		languages.remove(language);
		if(language.equals(currentLanguage))
			currentLanguage = null;
		firePropertyChange(CHILD, language, null);
	}

	public Set<String> getAllKeys() {
		Set<String> keys = new HashSet<String>();
		for(Language lang : languages)
			keys.addAll(lang.keySet());
		for(Language lang : languages)
			lang.keySet().addAll(keys);
		return keys;
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
		Language oldCurrentLanguage = this.currentLanguage;
		this.currentLanguage = currentLanguage;
		firePropertyChange(CHILD, oldCurrentLanguage, currentLanguage);
	}

	public void removeObserver(SationObserver observer) {
		observers.remove(observer);
	}

	public void updateObservers() {
		if (currentLanguage == null)
			if(getLanguages().size() > 0 )
				currentLanguage = getLanguages().get(0);
		if(currentLanguage != null){
			for(SationObserver observer : observers){
				String key = observer.getI18nKey();
				observer.setValue(currentLanguage.get(key));
			}
		}
	}

	@Override
	public void resetStatus() {
	}

	public void updateAllLanguages() {
		for(Language language : languages){
			language.updateLanguage();
		}
		updateObservers();
		firePropertyChange(INPUT, null, languages);
	}
}
