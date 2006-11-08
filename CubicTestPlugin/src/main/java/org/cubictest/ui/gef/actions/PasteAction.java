/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;


public class PasteAction extends SelectionAction {
	
	/**
	 * The Actions ID which can be used to set and get it from acion registers.
	 */
	public static final String ACTION_ID = "cubicTestPlugin.action.present";
	
	public PasteAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		if (getClipboardContents() != null)
			return true;
		return false;
	}
	
	@Override
	protected void init(){
		setId(ActionFactory.PASTE.getId());
		setText("Paste");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages(); 
		setImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(false);
	}
	
	@Override
	public void run() {
		Object obj = getClipboardContents();
		if (!(obj instanceof List))
			return;
		
		List list = (List) obj;
		
		for (Iterator iter = list.iterator(); iter.hasNext();){
			Object next = iter.next();
			if(!(next instanceof EditPart))
				return;

			EditPart clipboardItem = (EditPart) next;
			
			StructuredSelection selection = (StructuredSelection)getSelection();
			
			Object selectedObj = selection.getFirstElement();
			if (!(selectedObj instanceof EditPart))
				return;
			
			EditPart target= (EditPart)selectedObj;
			
			if(clipboardItem.getModel() instanceof PageElement){
				PageElement element = (PageElement) clipboardItem.getModel();
				if (target.getModel() instanceof PageElement)
					target = target.getParent();
				if (target.getModel() instanceof IContext){
					try {
						CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
						createElementCmd.setContext((IContext)target.getModel());
						createElementCmd.setPageElement(element.clone());
						createElementCmd.setIndex(target.getChildren().size());
						getCommandStack().execute(ViewUtil.getCompoundCommandWithResize(createElementCmd, ViewUtil.ADD, target));
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				
			}else if(clipboardItem.getModel() instanceof AbstractPage){
				AbstractPage page = (AbstractPage) clipboardItem.getModel();
				if (target.getModel() instanceof PageElement)
					target = target.getParent();
				while (target.getModel() instanceof IContext)
					target = target.getParent();
				if (target.getModel() instanceof Test){
					try {
						AbstractPage clone = page.clone();
						Point p = page.getPosition();
						p.x = p.x + 25;
						p.y = p.y + 25;
						clone.setPosition(p);
						((Test)target.getModel()).addPage(clone);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Object getClipboardContents() {
		return Clipboard.getDefault().getContents();
	}
	

}
