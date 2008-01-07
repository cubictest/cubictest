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
package org.cubictest.ui.gef.view;

import org.cubictest.model.TestPartStatus;
import org.eclipse.draw2d.ColorConstants;


/**
 * @author Christian Schwarz
 *
 */
public class ExtensionStartPointFigure extends SubTestFigure {
	

	public ExtensionStartPointFigure(String name){
		super(name);
		setLabelLength(200);
		setForegroundColor(ColorConstants.white);
		setBackgroundColor(ColorConstants.buttonDarker);
	}

	public void setStatus(TestPartStatus status){
		if(status == null){
			return;
		}
		switch (status){
			case UNKNOWN:
				setForegroundColor(ColorConstants.white);
				setBackgroundColor(ColorConstants.buttonDarker);
				break;		
			case PASS:
				setForegroundColor(ColorConstants.black);
				setBackgroundColor(ColorConstants.green);
				break;
			case WARN:
				setForegroundColor(ColorConstants.black);
				setBackgroundColor(ColorConstants.yellow);
				break;
			case FAIL:
				setForegroundColor(ColorConstants.black);
				setBackgroundColor(ColorConstants.red);
				break;
			case EXCEPTION:
				setForegroundColor(ColorConstants.black);
				setBackgroundColor(ColorConstants.orange);;
				break;
		}
	}
}
