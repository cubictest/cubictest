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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.FileUtil;
import org.cubictest.model.parameterization.Parameter;
import org.cubictest.model.parameterization.ParameterList;
import org.eclipse.core.resources.IFile;


public class ParameterPersistance {

	/**
	 * Writes properties to the given IFile. For Eclipse-internal use.
	 * 
	 * @param paramList The ParameterList to save.
	 * @param iFile The file to save to.
	 */
	public static void saveToFile(ParameterList paramList, IFile iFile) {
		File f = iFile.getLocation().toFile();
		saveToFile(paramList, f);
	}
	
	public static void saveToFile(ParameterList paramList, File file) {
		String xml = new CubicTestXStream().toXML(paramList);
		try {
			String charset = TestPersistance.getCharset(file);
			String charsetHeader = TestPersistance.getCharsetHeader(charset);
			xml = charsetHeader + "\n" + xml;
			FileUtils.writeStringToFile(file, xml, charset);
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
	}

	/**
	 * Reads a paramaterList from IFile.
	 * 
	 * @param iFile The file containing the test.
	 * @return The parameterList.
	 */	
	public static ParameterList loadFromFile(String fileName) {
		File file = FileUtil.getFileFromWorkspaceRoot(fileName);
		String xml = "";
		try {
			String charset = TestPersistance.getCharset(file);
			xml = FileUtils.readFileToString(file, charset);
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
		try {
			ParameterList list = (ParameterList) new CubicTestXStream().fromXML(xml);
			list.setFileName(fileName);
			return list;
		} catch (Exception e) {
			return manualParseFromFile(fileName);
		}
	}
	
	private static ParameterList manualParseFromFile(String fileName) {
		File file = FileUtil.getFileFromWorkspaceRoot(fileName);
		ParameterList list = new ParameterList();
		list.setFileName(fileName);
		FileReader fr;
		try {
			fr = new FileReader(file);
		
			BufferedReader bw = new BufferedReader(fr);
			String line = bw.readLine();
			if (line == null)
				return list;
			
			for(String token : line.split(";")){
				Parameter param = new Parameter();
				param.setHeader(token.trim());
				list.addParameter(param);
			}
			line = bw.readLine();
			while (line != null){
				int i = 0;
				for(String token : line.split(";")){
					list.getParameters().get(i++).addParameterInput(token.trim());
				}
				line = bw.readLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		return list;
	}
}
