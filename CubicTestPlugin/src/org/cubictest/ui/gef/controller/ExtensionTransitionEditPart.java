/*
 * Created on 09.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.model.ExtensionTransition;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;

public class ExtensionTransitionEditPart extends TransitionEditPart {

	private CubicTestLabel label;

	public ExtensionTransitionEditPart( ExtensionTransition transition) {
		super(transition);

	}
	
	@Override
	public IFigure getFigure() {
		PolylineConnection conn = (PolylineConnection) super.getFigure();
		
		label = new CubicTestLabel(getModel().getExtensionPoint().getName());
		ConnectionEndpointLocator locator = new ConnectionEndpointLocator(conn,true);
		locator.setUDistance(20);
		conn.add(label,locator);
		
		return conn;
	}
	
	@Override
	public ExtensionTransition getModel() {

		return (ExtensionTransition) super.getModel();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		label.setText(getModel().getExtensionPoint().getName());
	}
}
