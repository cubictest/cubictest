/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.watir.delegates;

import org.cubictest.common.converters.interfaces.ICustomTestStepConverter;
import org.cubictest.common.exception.CubicException;
import org.cubictest.model.CustomTestStep; 
import org.cubictest.export.watir.interfaces.IStepList;

/**
 * Watir custom test step converter.
 * 
 * @author chr_schwarz
 */
public class CustomTestStepConverter implements ICustomTestStepConverter<IStepList> {

	public void handleCustomStep(IStepList t, CustomTestStep cts) {
		throw new CubicException("Custom test step not supported in Watir exporter");
	}

}
