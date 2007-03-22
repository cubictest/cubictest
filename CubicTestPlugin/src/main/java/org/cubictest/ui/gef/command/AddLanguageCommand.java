package org.cubictest.ui.gef.command;

import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.eclipse.gef.commands.Command;

public class AddLanguageCommand extends Command {

	private Language language;
	private AllLanguages allLanguages;

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setAllLanguages(AllLanguages allLanguages) {
		this.allLanguages = allLanguages;
	}

	@Override
	public void execute() {
		super.execute();
		allLanguages.addLanguage(language);
	}
	
	@Override
	public void undo() {
		super.undo();
		allLanguages.removeLanguage(language);
	}
}
