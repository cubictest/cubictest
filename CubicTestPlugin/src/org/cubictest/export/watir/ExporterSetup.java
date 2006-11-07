/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.watir;

import org.cubictest.common.converters.PageWalker;
import org.cubictest.common.converters.TreeTestWalker;
import org.cubictest.export.watir.delegates.ContextConverter;
import org.cubictest.export.watir.delegates.CustomTestStepConverter;
import org.cubictest.export.watir.delegates.PageElementConverter;
import org.cubictest.export.watir.delegates.UrlStartPointConverter;
import org.cubictest.export.watir.delegates.TransitionConverter;
import org.cubictest.export.watir.interfaces.IStepList;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.operation.IRunnableWithProgress;


/**
 * This class is the entry point for all Watir export.
 * It sets up a runnable directory walker with Watir implementations of the needed interfaces.
 * Watir code is the goal output.
 */
public class ExporterSetup {

	/**
	 * Provides a runnable directory walker for Watir export.
	 * Sets up the Watir implementation of the relavant interfaces.
	 * @param res The resource that contains the tests. Either a .aat file or a directory.
	 */
	public static IRunnableWithProgress getRunnableExporter(IResource resource) {
		
		//Set up dependency hierarchy:
		TreeTestWalker<IStepList> testWalker = buildWatirTestWalker();

		//The walker takes them all as input:
		String fileExtension = "rb";
		IRunnableWithProgress walker = 
			new DirectoryWalker(resource, testWalker, fileExtension);
		
		//Someone must invoke run() on this walker.
		return walker;
		
	}

	/**
	 * Builds a test converter with Watir implementations as building blocks / delegates.
	 */
	public static TreeTestWalker<IStepList> buildWatirTestWalker() {

		PageWalker<IStepList> pw = new PageWalker<IStepList>(PageElementConverter.class, ContextConverter.class);
		TreeTestWalker<IStepList> testWalker = new TreeTestWalker<IStepList>(UrlStartPointConverter.class, pw, 
				TransitionConverter.class,CustomTestStepConverter.class);
		
		return testWalker;
	}


}
