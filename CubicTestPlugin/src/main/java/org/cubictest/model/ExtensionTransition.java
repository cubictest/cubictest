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
package org.cubictest.model;

/**
 * 
 * Transition from an ExtensionPoint in another Test to a Page/State in current test.
 * 
 * @author Christian Schwarz
 */
public class ExtensionTransition extends Transition {

	private String id; //the source page ID
	
	private transient ExtensionPoint extensionPoint;

	public ExtensionTransition() {}
	
	public ExtensionTransition(TransitionNode sourceNode, TransitionNode targetNode, ExtensionPoint extensionPoint) {
		super(sourceNode, targetNode);
		this.id = extensionPoint.getPageId();
	}
	
	public ExtensionPoint getExtensionPoint() {
		if(extensionPoint == null) {
			for(AbstractPage page : ((SubTest)getStart()).getTest(false).getPages()) {
				if(page.getId() == id) {
					for(Transition t : page.getOutTransitions()) {
						if(t.getEnd() instanceof ExtensionPoint) {
							extensionPoint = (ExtensionPoint) t.getEnd();
						}
					}
				}
			}
		}
		return extensionPoint;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": ExtensionPoint (start) = " + getExtensionPoint() + ", end = " + getEnd();
	}
}
