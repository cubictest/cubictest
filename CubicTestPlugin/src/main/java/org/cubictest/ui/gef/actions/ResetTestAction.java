/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
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
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;


public class ResetTestAction extends EditorPartAction {
	public static final String ACTION_ID = "cubicTestPlugin.action.resetTest";
	public ResetTestAction(IEditorPart editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	protected void init() {
		super.init();
		setText("Reset Test-Run");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		super.run();
		GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
		Test test = testEditor.getTest();
		test.resetStatus();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.RESET_TEST_IMAGE);
	}

}
