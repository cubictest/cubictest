/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.resources.UiText;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.SubTest;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.policies.TestComponentEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author SK Skytteren
 * Contoller for the <code>ExtensionPoint</code> model.
 *
 */
public class CustomTestStepEditPart extends AbstractNodeEditPart implements IPropertySource {

	private AbstractTransitionNodeFigure customTestStepFigure;

	/**
	 * Constructor for <code>ExtensionPointEditPart</code>.
	 * @param step the model
	 */
	public CustomTestStepEditPart(CustomTestStep step) {
		setModel(step);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		String name = ((CustomTestStep)getModel()).getDisplayText();
		customTestStepFigure = new AbstractTransitionNodeFigure();
		customTestStepFigure.setBackgroundColor(ColorConstants.cyan);
		Point p = ((TransitionNode)getModel()).getPosition();
		customTestStepFigure.setLocation(p);
		customTestStepFigure.setText(name);
		customTestStepFigure.setToolTipText("Custom test step: $labelText");
		return customTestStepFigure;
	}
	
	public void updateParams() {
		refresh();
		refreshVisuals();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TestComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		ConnectionPoint connectionPoint = (ConnectionPoint)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x,position.y,-1,-1);
		customTestStepFigure.setText(((CustomTestStep)getModel()).getDisplayText());
		((TestEditPart)getParent()).setLayoutConstraint(this,figure,r);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	public void performRequest(Request request) {
		
		if(request.getType() == RequestConstants.REQ_OPEN){
			final IFile file = ((CustomTestStep)getModel()).getClassSrcPath();
			if (!file.exists() || !(file instanceof IFile)) {
				MessageDialog.openError(new Shell(), UiText.APP_TITLE, 
						"Container \"" + ((SubTest)getModel()).getFilePath() + "\" does not exist.");
				return;
			}

			(new Shell()).getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
					}
				}
			});
		}
		super.performRequest(request);
	}

	public Object getEditableValue() {
		return ((CustomTestStep) getModel()).getConfig();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		
		CustomTestStep cts = (CustomTestStep) getModel();
		
		for(String property : cts.getArgumentNames()) {
			TextPropertyDescriptor pd = new TextPropertyDescriptor(property, property);
			properties.add(pd);
		}

		IPropertyDescriptor[] ipdA = (IPropertyDescriptor[])properties.toArray( new IPropertyDescriptor[] {});

		return ipdA;
	}

	public Object getPropertyValue(Object arg0) {
		String property = (String) arg0;
		CustomTestStep cts = (CustomTestStep) getModel();
		if(cts.getConfig().get(property) != null) {
			return cts.getConfig().get(property);
		}
		return "";
	}

	public boolean isPropertySet(Object property) {
		return ((CustomTestStep) getModel()).getConfig().get((String) property) != null;
	}

	public void resetPropertyValue(Object property) {
		((CustomTestStep) getModel()).getConfig().remove((String) property);
	}

	public void setPropertyValue(Object property, Object value) {
		((CustomTestStep) getModel()).getConfig().put((String) property, (String) value);
	}
		
	@Override
	public boolean canBeTargetForPaste() {
		return false;
	}

}