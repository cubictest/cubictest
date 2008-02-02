/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.command;

import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.eclipse.gef.commands.Command;

public class AddLanguageCommand extends Command {

	private Language language;
	private AllLanguages allLanguages;
	private Language oldCurrentLanguage;

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
		oldCurrentLanguage = allLanguages.getCurrentLanguage();
		allLanguages.setCurrentLanguage(language);
	}
	
	@Override
	public void undo() {
		super.undo();
		allLanguages.setCurrentLanguage(oldCurrentLanguage);
		allLanguages.removeLanguage(language);
	}
}
