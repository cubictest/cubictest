/*
 * Created on 11.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.ChangeAbstractPageNameCommand;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.directEdit.CubicTestEditorLocator;
import org.cubictest.ui.gef.policies.AbstractPageDirectEditPolicy;
import org.cubictest.ui.gef.policies.ContextContainerEditPolicy;
import org.cubictest.ui.gef.policies.ContextLayoutEditPolicy;
import org.cubictest.ui.gef.policies.PageNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


public abstract class AbstractPageEditPart extends AbstractNodeEditPart 
		implements IPropertySource{
	
	private CubicTestDirectEditManager manager;


	public Object getEditableValue() {
		return this;
	}
	/*
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		int i = 0;
		List<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		TextPropertyDescriptor pd = new TextPropertyDescriptor(this, 
				i++ + ": " + getType() + " - Description:");
		properties.add(pd);
		
		for (Object object : getChildren()){
			if (object instanceof IPropertySource){
				if (object instanceof PageElementEditPart){
					PageElementEditPart child = (PageElementEditPart)object;
					properties.add(new TextPropertyDescriptor(child,
							i++ + ": " + child.getType()));
				}
			}
		}
		IPropertyDescriptor[] ipdA = (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});

		return ipdA;
	}

	protected abstract String getType();
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if (id.equals(this)) return ((AbstractPage)getModel()).getName();
		else {
			for (Object child:getChildren()){
				if (child.equals(id)){
					return child;
				}
			}
		}
		return "getProperty Value " + id.toString();
		//return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(this)){
			AbstractPage page = ((AbstractPage)getModel());			
			ChangeAbstractPageNameCommand cmd = new ChangeAbstractPageNameCommand();
			cmd.setAbstractPage(page);
			cmd.setOldName(page.getName());
			cmd.setName((String) value);
			getViewer().getEditDomain().getCommandStack().execute(cmd);
		}else {
			for (Object child : getChildren()){
				if (child.equals(id)){
					((IPropertySource)child).setPropertyValue(id,value);
				}
			}
		}
	}

	private void startDirectEdit() {
		if (manager == null)
				manager = new CubicTestDirectEditManager(this,
						TextCellEditor.class,
						new CubicTestEditorLocator(((CubicTestGroupFigure)getFigure()).getHeader()),
						((AbstractPage)getModel()).getName());
			manager.setText(((AbstractPage)getModel()).getName());
			manager.show();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request request) {
		
		if(request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN){
			startDirectEdit();
		}
		super.performRequest(request);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		CubicTestGroupFigure figure = new CubicTestGroupFigure(((AbstractPage)getModel()).getName());
		figure.setLocation(((TransitionNode)getModel()).getPosition());
		return figure;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		ContextLayoutEditPolicy layoutPolicy = new ContextLayoutEditPolicy((IContext)getModel());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, layoutPolicy);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new PageNodeEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContextContainerEditPolicy(layoutPolicy));
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new AbstractPageDirectEditPolicy());
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren(){
		return ((AbstractPage)getModel()).getElements();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
	public void setSelected(int value) {
		super.setSelected(value);
		if (value != EditPart.SELECTED_NONE)
			((CubicTestGroupFigure)getFigure()).setSelected(true);
		else
			((CubicTestGroupFigure)getFigure()).setSelected(false);
		getFigure().repaint();
		
		//If this is the last element created, start direct edit:
		CommandStack stack = getViewer().getEditDomain().getCommandStack();
		if (manager == null && ViewUtil.pageHasJustBeenCreated(stack, (AbstractPage)getModel())) {
			startDirectEdit();			
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals(){
		AbstractPage page = (AbstractPage)getModel();
		CubicTestGroupFigure figure = (CubicTestGroupFigure) getFigure();
		String title = ((AbstractPage)getModel()).getName();
		figure.setText(title);
		Point position = page.getPosition();
		Dimension dimension = page.getDimension();
		Rectangle r = new Rectangle(position.x, position.y, dimension.width, dimension.height);
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
		if (manager !=null)
			manager.setText(title);
	}
}
