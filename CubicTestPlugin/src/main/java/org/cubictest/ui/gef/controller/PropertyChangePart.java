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
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.TestPartStatus;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;


/**
 * An abstract controller that listents to the <code>PropertyAwareObject</code>.
 * 
 * @author SK Skytteren
 */
public abstract class PropertyChangePart extends AbstractGraphicalEditPart implements
		PropertyChangeListener {

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate(){
		super.activate();
		PropertyAwareObject propertyAwareObject = (PropertyAwareObject) getModel();
		propertyAwareObject.addPropertyChangeListener(this);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate(){
		super.deactivate();
		PropertyAwareObject propertyAwareObject = (PropertyAwareObject) getModel();
		propertyAwareObject.removePropertyChangeListener(this);
	}

	/*
	 *  (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt){
		String property = evt.getPropertyName();
		if (PropertyAwareObject.CHILD.equals(property))
			handleChildChange(evt);
		else if (PropertyAwareObject.OUTPUT.equals(property))
			handleOutputChange(evt);
		else if (PropertyAwareObject.INPUT.equals(property))
			handleInputChange(evt);
		else if (PropertyAwareObject.BOUNDS.equals(property));

		else if (PropertyAwareObject.NAME.equals(property)){
			GraphicalEditPart graphicalEditPart = (GraphicalEditPart) (getViewer().getContents());
			IFigure partFigure = graphicalEditPart.getFigure();
			partFigure.getUpdateManager().performUpdate();
		}
		else if (PropertyAwareObject.STATUS.equals(property))
			handleStatusChange(evt);
		else if (PropertyAwareObject.PARAM.equals(property)){
			if(this instanceof TestEditPart){
				((TestEditPart)this).updateParams();
			}
		}
		else if (PageElement.NOT.equals(property)){
			if(this instanceof PageElementEditPart){
				((PageElementEditPart)this).refresh();
			}
		}
		refreshChildren();
		refreshVisuals();
		refresh();
	}
	
	private void handleStatusChange(PropertyChangeEvent evt) {
		TestPartStatus newStatus = (TestPartStatus)evt.getNewValue();
		if (getFigure() instanceof TestStepLabel) {
			((TestStepLabel)getFigure()).setStatus(newStatus);
		}
	}

	private void handleInputChange(PropertyChangeEvent evt){
		Object newValue= evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (!((oldValue != null) ^ (newValue != null)))
			throw new IllegalStateException("Only one of old or new values must be non-null for INPUT event");

		if (newValue != null){

			ConnectionEditPart editPart = createOrFindConnection(newValue);
			int modelIndex = getModelTargetConnections().indexOf(newValue);
			addTargetConnection(editPart, modelIndex);
		}else{
			List children = getTargetConnections();
			ConnectionEditPart partToRemove = null;
			for (Iterator iter = children.iterator(); iter.hasNext();){
				ConnectionEditPart part = (ConnectionEditPart) iter.next();
				if (part.getModel() == oldValue){
					partToRemove = part;
					break;
				}
			}
			if (partToRemove != null)
				removeTargetConnection(partToRemove);
		}
		getContentPane().revalidate();
	}

	private void handleOutputChange(PropertyChangeEvent evt){

		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (!((oldValue != null) ^ (newValue != null))){
			throw new IllegalStateException("Only one of old or new values must be non-null for INPUT event");
		}

		if (newValue != null){
			ConnectionEditPart editPart = createOrFindConnection(newValue);
			int modelIndex = getModelSourceConnections().indexOf(newValue);
			addSourceConnection(editPart, modelIndex);
		}else{
			List children = getSourceConnections();

			ConnectionEditPart partToRemove = null;
			for (Iterator iter = children.iterator(); iter.hasNext();){
				ConnectionEditPart part = (ConnectionEditPart) iter.next();
				if (part.getModel() == oldValue){
					partToRemove = part;
					break;
				}
			}
			if (partToRemove != null)
				removeSourceConnection(partToRemove);
		}
		getContentPane().revalidate();
	}

	private void handleChildChange(PropertyChangeEvent evt){
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (!((oldValue != null) ^ (newValue != null))){
			throw new IllegalStateException("Only one of old or new values must be non-null for CHILD event");
		}
		if (newValue != null){
			EditPart editPart = createChild(newValue);
			int modelIndex = getModelChildren().indexOf(newValue);
			addChild(editPart, modelIndex);
		}else{
			List children = getChildren();

			EditPart partToRemove = null;
			for (Iterator iter = children.iterator(); iter.hasNext();){
				EditPart part = (EditPart) iter.next();
				if (part.getModel() == oldValue){
					partToRemove = part;
					break;
				}
			}
			if (partToRemove != null)
				removeChild(partToRemove);
		}
	}
	
	/**
	 * Get whether part is cuttable.
	 * Should be overrided by parts that are not cuttable.
	 * @return whether part is cuttable.
	 */
	public boolean isCuttable() {
		return true;
	}
	
	/**
	 * Get whether part is copyable.
	 * Should be overrided by parts that are not copyable.
	 * @return whether part is copyable.
	 */
	public boolean isCopyable() {
		return true;
	}
	
}