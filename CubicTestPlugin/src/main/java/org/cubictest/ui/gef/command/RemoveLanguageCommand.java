package org.cubictest.ui.gef.command;

import org.cubictest.model.Test;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.eclipse.gef.commands.Command;

public class RemoveLanguageCommand extends Command {

	private Test test;
	private AllLanguages allLanuages;
	private Language language;
	private Language currentLanguage;

	public void setTest(Test test) {
		this.test = test;
	}

	public void setAllLanguages(AllLanguages allLanuages) {
		this.allLanuages = allLanuages;	
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	@Override
	public void execute() {
		super.execute();
		if(language.equals(allLanuages.getCurrentLanguage()))
			currentLanguage = language;
		allLanuages.removeLanguage(language);
		test.updateObservers();
	}
	
	@Override
	public void undo() {
		super.undo();
		allLanuages.addLanguage(language);
		if(currentLanguage != null)
			allLanuages.setCurrentLanguage(currentLanguage);
		test.updateObservers();
	}

}
