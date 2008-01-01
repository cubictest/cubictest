/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.cubictest.ui.utils.ModelUtil;
import org.cubictest.ui.utils.ViewUtil;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.INewWizard;


/**
 * Wizard for creating new sub tests.  The wizard creates one file with the extension "aat".
 * If the container resource (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target container.
 * 
 * @author Christian Schwarz 
 */

public class NewSubTestWizard extends AbstractNewSimpleStartPointTestWizard implements INewWizard {
	
	protected List<TransitionNode> refactorInitOriginalNodes;
	protected CommandStack commandStack;
	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		testDetailsPage = new TestDetailsPage(selection, !extensionPointMap.isEmpty(), "sub test");
		addPage(testDetailsPage);
	}
	
	public Test createEmptyTest(String name, String description) {
		Test emptyTest = WizardUtils.createEmptyTestWithSubTestStartPoint("test" + System.currentTimeMillis(), name, description);
		if (refactorInitOriginalNodes != null) {
			try {
				emptyTest.getPages().clear();
				emptyTest.getTransitions().clear();
				emptyTest.getStartPoint().getOutTransitions().clear();
				List<TransitionNode> newNodes = ViewUtil.cloneAndAddNodesToTest(emptyTest, refactorInitOriginalNodes, commandStack, false);
				if (ModelUtil.getFirstNode(newNodes) instanceof Page) {
					SimpleTransition startTransition = new SimpleTransition(emptyTest.getStartPoint(), 
							ModelUtil.getFirstNode(newNodes));	
					emptyTest.addTransition(startTransition);
				}
			} catch (CloneNotSupportedException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow("Unable to create sub test.", e);
			}
		}
		return emptyTest;
	}

	protected void getWizardTitle() {
		setWindowTitle("New CubicTest sub test");
	}

	public void setRefactorInitOriginalNodes(
			List<TransitionNode> refactorInitOriginalNodes) {
		this.refactorInitOriginalNodes = refactorInitOriginalNodes;
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

}