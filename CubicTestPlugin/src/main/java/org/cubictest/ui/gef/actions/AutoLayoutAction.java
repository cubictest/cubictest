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

package org.cubictest.ui.gef.actions;

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;


public class AutoLayoutAction extends EditorPartAction {

	public static final String ACTION_ID = "cubicTestPlugin.action.autoLayout";
	
	public AutoLayoutAction(IEditorPart editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	protected void init() {
		super.init();
		setText("Auto-Layout");
		setId(ACTION_ID);
	}
	

	@Override
	public void run() {
		GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
		AutoLayout manager = new AutoLayout(testEditor);
		for(Transition t : ((Test)testEditor.getGraphicalViewer().getContents().getModel()).getStartPoint().getOutTransitions()) {
			manager.layout(t.getEnd());
		}
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.AUTO_LAYOUT_IMAGE);
	}
}
