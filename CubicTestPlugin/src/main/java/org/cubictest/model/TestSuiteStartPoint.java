/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * Start point that does not invoke any actions. Can only be used in test suites.
 * 
 * @author Christian Schwarz
 */
public class TestSuiteStartPoint extends ConnectionPoint implements IStartPoint {
	
	@Override
	public void setInTransition(Transition inTransition) {
		inTransition.getStart().removeOutTransition(inTransition);
	}
	
}
