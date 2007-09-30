/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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
