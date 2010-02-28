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
package org.cubictest.ui.utils;

import java.util.List;

import org.cubictest.model.IdentifierType;

/**
 * Util class for IdentifierTypes. 

 * @author chr_schwarz
 */
public class IdentifierUtil {


	public static int indexOf(IdentifierType idType, List<IdentifierType> list) {
		int i = 0;
		for (IdentifierType type : list) {
			if (type.equals(idType)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public static String[] toStringArray(List<IdentifierType> list) {
		String[] array = new String[list.size()];
		int i = 0;
		for (IdentifierType type : list) {
			array[i] = type.toString();
			i++;
		}
		return array;
	}
	
}
