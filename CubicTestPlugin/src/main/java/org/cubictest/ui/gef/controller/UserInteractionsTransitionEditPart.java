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
import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.AbstractTextInput;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;

/**
 * Controller for user interactions transition.
 * 
 * @author Christian Schwarz
 *
 */
public class UserInteractionsTransitionEditPart extends TransitionEditPart{
	static int MAX_LABEL_LENGTH = 40;
	
	private Figure figure;
	private boolean listenersAdded;
	
	
	/**
	 * Constructor for the <code>UserInteractionsTransitionEditPart</code>.
	 * @param transition
	 */	
	public UserInteractionsTransitionEditPart(UserInteractionsTransition userInteractionsTransition){
		super(userInteractionsTransition);
		listenersAdded = false;
	}
	
	@Override
	public void activate() {
		super.activate();
		if (!listenersAdded) {
			addPageElementListeners();
			listenersAdded = true;
		}
	}

	
	@Override
	public void deactivate() {
		super.deactivate();
		if (listenersAdded) {
			removePageElementListeners();
			listenersAdded = false;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		refreshVisuals();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		UserInteractionsTransition transition = (UserInteractionsTransition) getModel();
		PolylineConnection conn = (PolylineConnection) getFigure();
		List<Figure> delete = new ArrayList<Figure>();
		for(Object o : conn.getChildren()){
			if(o instanceof Figure){
				Figure f = (Figure)o;
				if(!(f instanceof PolygonDecoration) && ColorConstants.menuBackground.equals(f.getBackgroundColor()))
					delete.add(f);
			}
		}
		for(Figure f : delete)
			conn.remove(f);
		
		if (transition.hasUserInteractions()){
			figure = new Figure();
			ToolbarLayout layout = new ToolbarLayout();
			layout.setVertical(true);
			layout.setStretchMinorAxis(true);
			figure.setLayoutManager(layout);
			figure.setBackgroundColor(ColorConstants.menuBackground);
			figure.setOpaque(true);
			figure.setToolTip(new Label("User interaction on the page/state"));
			conn.setToolTip(new Label("User interaction on the page/state"));
			
			List<UserInteraction> actions = transition.getUserInteractions();
			Label inputLabel;
			
			for(UserInteraction action : actions){
				IActionElement element = action.getElement();
				String text = "";
				if(element != null) {
					text = getText(element);
					if(text.length() > MAX_LABEL_LENGTH) {
						text = text.substring(0, MAX_LABEL_LENGTH - 3) + "...";
					}
					text += ": ";
				}
				String inputLabelText = text + action.getActionType().getText();
				
				if (ActionType.ENTER_TEXT.equals(action.getActionType()) 
						&& element instanceof AbstractTextInput) {
					inputLabelText =  inputLabelText + ": "
						+ action.getTextualInput();
				}
				if (ActionType.ENTER_PARAMETER_TEXT.equals(action.getActionType())
						&& element instanceof AbstractTextInput) {
					inputLabelText =  inputLabelText + ": "
						+ action.getTextualInput();
				}
				inputLabel = new Label(inputLabelText);
				if (element instanceof Link){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.LINK_IMAGE));
				}
				else if (element instanceof Image){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.IMAGE_IMAGE));
				}
				else if (element instanceof TextField){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.TEXT_FIELD_IMAGE));
				}
				else if (element instanceof Button){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.BUTTON_IMAGE));
				}
				else if (element instanceof Checkbox){
					if (action.getActionType().equals(ActionType.UNCHECK)) {
						inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE));
					}
					else {
						inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.CHECKBOX_CHECKED_IMAGE));
					}
				}
				else if (element instanceof RadioButton){
					if (action.getActionType().equals(ActionType.UNCHECK)) {
						inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.RADIO_BUTTON_UNCHECKED_IMAGE));
					}
					else {
						inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.RADIO_BUTTON_CHECKED_IMAGE));
					}
				}
				else if (element instanceof Select){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.SELECT_IMAGE));
				}
				else if (element instanceof Option){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.OPTION_IMAGE));
				}
				else if (element instanceof TextArea){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.TEXT_AREA_IMAGE));
				}
				else if (element instanceof Password){
					inputLabel.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.PASSWORD_IMAGE));
				}	
				else {
					inputLabel = new Label(inputLabelText);
				}
				inputLabel.setLabelAlignment(PositionConstants.LEFT);
				figure.add(inputLabel);
			}
			
			ConnectionEndpointLocator locator = new ConnectionEndpointLocator(conn,true);
			conn.add(figure,locator);
		}
	}
	
	/**
	 * Sets the width of the line when selected
	 */
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		if (value != EditPart.SELECTED_NONE)
			((PolylineConnection) getFigure()).setLineWidth(2);
		else
			((PolylineConnection) getFigure()).setLineWidth(1);
	}
	
	private String getText(IActionElement element) {
		if (element instanceof PageElement) {
			PageElement pe = (PageElement) element;
			return pe.getDirectEditIdentifier().getValue();
		}
		else {
			return element.getDescription();						
		}
	}
	
	private void addPageElementListeners() {
		List<UserInteraction> actions = ((UserInteractionsTransition) getModel()).getUserInteractions();
		for (UserInteraction action : actions) {
			PropertyAwareObject element = (PropertyAwareObject) action.getElement();
			if (element != null && element instanceof PageElement) {
				PageElement pe = (PageElement) element;
				pe.getDirectEditIdentifier().addPropertyChangeListener(this);
			}
		}
	}
	
	private void removePageElementListeners() {
		List<UserInteraction> actions = ((UserInteractionsTransition) getModel()).getUserInteractions();
		for (UserInteraction action : actions) {
			PropertyAwareObject element = (PropertyAwareObject) action.getElement();
			if (element != null && element instanceof PageElement) {
				PageElement pe = (PageElement) element;
				pe.getDirectEditIdentifier().removePropertyChangeListener(this);
			}
		}
	}
	
}