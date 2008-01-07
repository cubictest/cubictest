/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Erlend S. Halvorsen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Erlend S. Halvorsen - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.htmlPrototype;

import org.cubictest.exporters.htmlPrototype.delegates.PageConverter;
import org.cubictest.exporters.htmlPrototype.delegates.PageElementConverter;
import org.cubictest.exporters.htmlPrototype.delegates.TestConverter;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.operation.IRunnableWithProgress;


/**
 * This class is the entry point for all HTML htmlSkeleton export.
 * It sets up a runnable directory walker with HTML skeleton implementations of the needed interfaces.
 * HTML skeleton is the goal output.
 */
public class ExporterSetup {
	
	/**
	 * Provides a runnable directory walker for HTML skeleton export.
	 * Sets up the HTML skeleton implementation of the relavant interfaces.
	 * @param res The resource that contains the tests. Either a test file or a directory.
	 */
	public static IRunnableWithProgress getRunnableExporter(IResource res) {	
		//The walker takes them all as input:
		String fileExtension = ".html";
		IRunnableWithProgress walker = new HtmlExportDirectoryWalker(res, new TestConverter(new PageConverter(fileExtension, new PageElementConverter())), fileExtension);
		
		//Someone must invoke run() on this walker.
		return walker;
		
	}
}
