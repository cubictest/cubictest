/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.cubictest.model.TestPartStatus;
import org.eclipse.draw2d.ColorConstants;


public class TestStepLabel extends CubicTestLabel{
	
	public TestStepLabel(String title){
		super(title);
		setStatus(TestPartStatus.UNKNOWN);
	}
	
	public void setStatus(TestPartStatus status){
		setOpaque(true);
		switch (status){
			case UNKNOWN:
				setOpaque(false);
			break;		
			case PASS:
				setBackgroundColor(ColorConstants.green);
				break;
			case FAIL:
				setBackgroundColor(ColorConstants.red);
				break;
			case EXCEPTION:
				setBackgroundColor(ColorConstants.orange);;
				break;
			default:
				setOpaque(false);
		}
	}
}
