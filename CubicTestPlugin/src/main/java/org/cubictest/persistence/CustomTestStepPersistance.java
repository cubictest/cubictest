/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.exception.TestNotFoundException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.customstep.CustomTestStep;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

import com.thoughtworks.xstream.io.StreamException;

public class CustomTestStepPersistance {
	
	
	public static CustomTestStep loadFromFile(IProject project, String fileName) {
		if (project == null) {
			//try to get file without project
			return loadFromFile(new File(fileName));
		}
		try {
			IFile ifile = project.getFile(new Path(fileName));
			return loadFromFile(ifile.getLocation().toFile());
		}
		catch (IllegalStateException e) {
			if (e.getMessage().indexOf("Workspace is closed") >= 0) {
				//Probably junit testing
				return loadFromFile(new File(fileName));
			}
		}
		throw new CubicException("Could not load custom step with file name = " + fileName);
	}

	
	public static CustomTestStep loadFromFile(File file) {
		String xml = "";
		try {
			String charset = TestPersistance.getCharset(file);
			xml = FileUtils.readFileToString(file, charset);
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
			String charset = TestPersistance.getCharset(file.getLocation().toFile());
			String charsetHeader = TestPersistance.getCharsetHeader(charset);
			xml = charsetHeader + "\n" + xml;
			FileUtils.writeStringToFile(file.getLocation().toFile(),
					xml, charset);
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
	}

}
