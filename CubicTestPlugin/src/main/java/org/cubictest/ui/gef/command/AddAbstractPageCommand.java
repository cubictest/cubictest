/*
 * Created on 06.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.factory.DataCreationFactory;
import org.eclipse.gef.commands.Command;


/**
 * A command for a new <code>AbstractPage</code> in a <code>Test</code>
 *
 * @author SK Skytteren
 */
public class AddAbstractPageCommand extends Command {

	private Test test;
	private AbstractPage page;
	private CreateTransitionCommand cmd;

	/**
	 * @param test
	 */
	public void setTest(Test test) {
		this.test = test;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		test.addPage(page);

		if (test.getPages().size() == 1 && page instanceof Page){
			DataCreationFactory fac = new DataCreationFactory(SimpleTransition.class);
			cmd = new CreateTransitionCommand();
			cmd.setSource(test.getStartPoint());
			cmd.setTarget(page);
			cmd.setTest(test);
			cmd.setTransition((SimpleTransition)fac.getNewObject());
			cmd.execute();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.removePage(page);
		if (cmd != null)
			cmd.undo();
	}

	public AbstractPage getPage() {
		return page;
	}

	/**
	 * @param page
	 */
	public void setPage(AbstractPage page) {
		this.page = page;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		test.addPage(page);
		if (cmd != null)
			cmd.redo();
	}
}
