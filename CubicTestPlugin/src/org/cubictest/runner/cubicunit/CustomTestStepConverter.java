/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.runner.cubicunit;

import org.cubictest.common.converters.interfaces.ICustomTestStepConverter;
import org.cubictest.model.CustomTestStep; 
import org.cubictest.runner.cubicunit.delegates.Holder;

public class CustomTestStepConverter implements ICustomTestStepConverter<Holder> {

	public void handleCustomStep(Holder holder, CustomTestStep cts) {
		cts.execute(holder.getContext(), holder.getDocument());
	}
}
