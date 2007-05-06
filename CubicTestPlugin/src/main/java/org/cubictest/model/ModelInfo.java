/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

/**
 * Class for holding track of incompatible model changes.
 * Use integer numbers.
 * 
 * The version number is independant of release versions, and can change frequently.
 * Should be updated as soon as incompatible changes are made.
 * Please also update the <code>LegacySupport</code> class to auto-convert old files to new standard.
 * 
 * @author chr_schwarz
 */
public class ModelInfo {

	public static String getCurrentModelVersion() {
		return "6";
	}
}
