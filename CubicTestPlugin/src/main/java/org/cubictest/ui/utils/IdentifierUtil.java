/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
			array[i] = type.displayValue();
			i++;
		}
		return array;
	}
	
}
