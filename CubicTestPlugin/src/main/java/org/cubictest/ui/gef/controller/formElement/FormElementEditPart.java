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
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;


/**
 * Abstract <code>EditPart</code>that provides functionality for the the other form edit parts.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public abstract class FormElementEditPart extends PageElementEditPart{

	CubicTestDirectEditManager manager;

}

