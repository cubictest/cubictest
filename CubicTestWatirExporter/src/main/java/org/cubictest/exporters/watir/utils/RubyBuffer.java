/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.utils;

/**
 * Holds lines of ruby code.
 * 
 * @author chr_schwarz
 */
public class RubyBuffer {

	private StringBuffer buffer;

	public RubyBuffer() {
		this.buffer = new StringBuffer();
	}
	
	
	/**
	 * Adds string with the specified indent.
	 * If text starts with tab, indenting will not take place.
	 * Adds newline after text if not already present.
	 */
	public void add(String s, int indent) {
		if (!s.startsWith("\t")) {
			for (int i = 0; i < indent; i++) {
				buffer.append("\t");			
			}
		}
		
		buffer.append(s);

		if (!s.endsWith("\n")) {
			buffer.append("\n");
		}
	}
	
	/**
	 * Add raw string to buffer.
	 * @param s the String to add.
	 */
	public void add(String s) {
		buffer.append(s);
	}
	
	
	public String toString() {
		return buffer.toString();
	}
}
