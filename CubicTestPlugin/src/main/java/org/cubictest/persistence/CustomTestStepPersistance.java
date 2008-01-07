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
package org.cubictest.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.exception.TestNotFoundException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.customstep.CustomTestStep;
import org.eclipse.core.resources.IFile;

import com.thoughtworks.xstream.io.StreamException;

public class CustomTestStepPersistance {

	public static CustomTestStep loadFromFile(IFile file) {
		String xml = "";
		try {
			xml = FileUtils.readFileToString(file.getLocation().toFile(), "ISO-8859-1");
		} catch (FileNotFoundException e) {
			Logger.error("Error loading test.", e);
			throw new TestNotFoundException(e.getMessage());
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
		CustomTestStep customStep = null;
		try{
			customStep = (CustomTestStep) new CubicTestXStream().fromXML(xml);
			return customStep;
		}catch(StreamException e){
		}
		if(customStep == null)
			customStep = new CustomTestStep();
		return customStep;
	}

	public static void saveToFile(CustomTestStep customStep, IFile file) {
		String xml = new CubicTestXStream().toXML(customStep);
		try {
			FileUtils.writeStringToFile(file.getLocation().toFile(),
					xml, "ISO-8859-1");
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
	}

}
