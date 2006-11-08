/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.converters.interfaces;

import org.cubictest.model.Transition;


/**
 * Interface converters of a transition to a list of test steps.
 * 
 * @author chr_schwarz
 */
public interface ITransitionConverter<T> {
	public void handleTransition(T t, Transition transition);

}
