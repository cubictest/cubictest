package org.cubictest.ui.gef.command;

import org.cubictest.model.Test;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.eclipse.gef.commands.Command;

public class ChangeCurrentLanguageCommand extends Command {

	private AllLanguages allLanguages;
	private Language newCurrentLanguage;
	private Language oldCurrentLanguage;
	private Test test;

	public void setAllLanguages(AllLanguages allLanguages) {
		this.allLanguages = allLanguages;
		oldCurrentLanguage = allLanguages.getCurrentLanguage();
	}

	public void setCurrentLanguage(Language currentLanguage) {
		this.newCurrentLanguage = currentLanguage;
	}
	
	public void setTest(Test test) {
		this.test = test;
	}
	
	@Override
	public void execute() {
		super.execute();
		allLanguages.setCurrentLanguage(newCurrentLanguage);
		test.updateObservers();
	}
	@Override
	public void undo() {
		super.undo();
		allLanguages.setCurrentLanguage(oldCurrentLanguage);
		test.updateObservers();
	}

}
