/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
	 * @param res The resource that contains the tests. Either a .aat file or a directory.
	 */
	public static IRunnableWithProgress getRunnableExporter(IResource res) {	
		//The walker takes them all as input:
		String fileExtension = ".html";
		IRunnableWithProgress walker = new HtmlExportDirectoryWalker(res, new TestConverter(new PageConverter(fileExtension, new PageElementConverter())), fileExtension);
		
		//Someone must invoke run() on this walker.
		return walker;
		
	}
}
