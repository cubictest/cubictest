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
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;


/**
 * @author Christian Schwarz
 *
 */
public class CustomTestStepFigure extends AbstractTransitionNodeFigure {
	

	private static final Color BASE_COLOR = new Color(null, 237, 209, 166);


	public CustomTestStepFigure(String name){
 		label = new CubicTestLabel(name);
 		label.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.CUSTOM_STEP_IMAGE));

		label.setTextPlacement(PositionConstants.MIDDLE);
		add(label);
		FlowLayout manager = ViewUtil.getFlowLayout();
		manager.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		manager.setMajorAlignment(FlowLayout.ALIGN_CENTER);
		setLayoutManager(manager);	
		
		setBackgroundColor(BASE_COLOR);
		setForegroundColor(ColorConstants.black);
		setBorder(new LineBorder(ColorConstants.black));
		setOpaque(true);
	}
	
	@Override
	public Dimension getPreferredSize(int hint, int hint2) {
		Dimension d = super.getPreferredSize(hint, hint2);
		d.height = 22;
		d.width = d.width + 7; 
		return d;
	}
	
	
	public void setStatus(TestPartStatus status){
		if(status == null){
			return;
		}
		switch (status){
			case UNKNOWN:
				setForegroundColor(ColorConstants.black);
				setBackgroundColor(BASE_COLOR);
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
