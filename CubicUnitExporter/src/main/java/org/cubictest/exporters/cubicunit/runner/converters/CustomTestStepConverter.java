/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.CustomTestStep;

public class CustomTestStepConverter implements ICustomTestStepConverter<Holder> {

	public void handleCustomStep(Holder holder, CustomTestStep cts) {
		//cts.execute(holder.getContext(), holder.getDocument());
	}
}
