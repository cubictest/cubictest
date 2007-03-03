/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPart;

public class AddPageElementAction extends SelectionAction {

	private Object selected;
	private Class<? extends PageElement> newElement;

	public AddPageElementAction(IWorkbenchPart part,Class<? extends PageElement> newElement) {
		super(part);
		this.newElement = newElement;
		setActionText();
	}

	/**
	 * The Actions ID which can be used to set and get it from acion registers.
	 */
	public static String getActionId(Class<? extends PageElement> element) {
		return "cubicTestPlugin.action.add" + ViewUtil.getType(element); 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		if(selected instanceof EditPart){
			Object model = ((EditPart)selected).getModel();
			if(model instanceof PageElement || model instanceof IContext){
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
	 */
	protected void setActionText() {
		try {
			setText("Add new " + newElement.newInstance().getType());
		} catch (InstantiationException e) {
			Logger.error(e, "Could not set action text.");
		} catch (IllegalAccessException e) {
			Logger.error(e, "Could not set action text.");
		}
    	setId(getActionId(newElement));
	}

	@Override
	public void runWithEvent(Event event){
		if(selected instanceof EditPart){
			EditPart editPart = (EditPart)selected;
			CreatePageElementCommand command = new CreatePageElementCommand();
			PageElement pe;
			try {
				if(newElement == null)
					return;
				pe = newElement.newInstance();
			} catch (Exception e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e,"Problems with adding the new element");
				return;
			}
			command.setPageElement(pe);
			Object model = (editPart).getModel();
			if(model instanceof IContext){
				command.setIndex(((IContext)model).getElements().size());
				command.setContext((IContext)model);
			}else if(model instanceof PageElement){
				editPart = editPart.getParent();
				IContext context = (IContext)editPart.getModel();
				command.setContext(context);
				command.setIndex(context.getElements().indexOf(model)+1);

			}else return;

			Command compoundCommand =
				ViewUtil.getCompoundCommandWithResize(command, ViewUtil.ADD, editPart);
			execute(compoundCommand);


			for(Object obj : editPart.getChildren()){
				if (obj instanceof EditPart) {
					EditPart ep = (EditPart) obj;
					if(ep.getModel().equals(pe)){
						ep.setSelected(EditPart.SELECTED_PRIMARY);
						break;
					}
				}
			}

		}
	}

	protected void handleSelectionChanged() {
		ISelection s = getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		selected = null;
		if (selection != null && selection.size() > 0) {
			selected = selection.toList().get(0);
		}
		refresh();
	}
}
