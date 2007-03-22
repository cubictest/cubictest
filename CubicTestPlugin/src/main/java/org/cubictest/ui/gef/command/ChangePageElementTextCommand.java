/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;


public class ChangePageElementTextCommand extends Command {

	private PageElement element;
	private String oldText, newText;
	private Identifier identifier;
	private boolean oldUseI18n;
	private boolean oldUseParam;
	
	public void setPageElement(PageElement element) {
		this.element = element;
	}
	public void setOldText(String oldText) {
		this.oldText = oldText;
	}
	public void setNewText(String newText) {
		this.newText = newText;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setText(newText);
		identifier = element.getDirectEditIdentifier();
		oldUseI18n = identifier.useI18n();
		oldUseParam = identifier.useParam();
		identifier.setUseI18n(false);
		identifier.setUseParam(false);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setText(oldText);
		identifier.setUseI18n(oldUseI18n);
		identifier.setUseParam(oldUseParam);
	}
}
