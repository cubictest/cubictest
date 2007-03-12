/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.converters;

import org.cubictest.export.IResultHolder;
import org.cubictest.model.UserInteractionsTransition;


/**
 * Interface converters of a transition to a list of test steps.
 * 
 * @author chr_schwarz
 */
public interface ITransitionConverter<T extends IResultHolder> {
	public void handleUserInteractions(T t, UserInteractionsTransition userInteractions);

}
