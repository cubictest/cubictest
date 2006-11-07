/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.converters.interfaces;

import org.cubictest.model.CustomTestStep;

public interface ICustomTestStepConverter<T> {
	public void handleCustomStep(T t, CustomTestStep cts);
}
