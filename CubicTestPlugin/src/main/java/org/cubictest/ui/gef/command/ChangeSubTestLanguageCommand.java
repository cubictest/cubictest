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
package org.cubictest.ui.gef.command;

import org.cubictest.model.SubTest;
import org.cubictest.model.i18n.Language;
import org.eclipse.gef.commands.Command;


/**
 * Command for changing language on subtest.
 * 
 * @author Christian Schwarz
 */
public class ChangeSubTestLanguageCommand extends Command {

	private Language language;
	private Language oldLanguage;
	private SubTest subtest;

	public void setNewLanguage(Language language) {
		this.language = language;
	}

	public void setTest(SubTest subtest){
		this.subtest = subtest;
	}
	
	@Override
	public void execute() {
		oldLanguage = subtest.getLanguage();
		subtest.setLanguage(language);
	}
	
	@Override
	public void undo() {
		subtest.setLanguage(oldLanguage);
	}
}
