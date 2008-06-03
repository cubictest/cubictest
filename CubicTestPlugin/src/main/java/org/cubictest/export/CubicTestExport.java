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
package org.cubictest.export;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.holders.IResultHolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.IDE;

/**
 * Interface for starting the export. Should be invoked by GUI action handlers.
 * 
 * @author Christian Schwarz
 */
public class CubicTestExport {

	public static <T extends IResultHolder> void exportSelection(IStructuredSelection selection,
			String outFileExtension,
			Class<? extends IUrlStartPointConverter<T>> urlSpc,
			Class<? extends ITransitionConverter<T>> tc,
			Class<? extends ICustomTestStepConverter<T>> ctsc,
			Class<? extends IPageElementConverter<T>> pec,
			Class<? extends IContextConverter<T>> cc,
			Class<? extends T> resultHolder) {
		
		IResource res = (IResource) selection.getFirstElement();

		TreeTestWalker<T> testWalker = new TreeTestWalker<T>(urlSpc, pec, cc, tc, ctsc);

		IDE.saveAllEditors(new IResource[] { res }, true);
		
		IRunnableWithProgress dirWalker = 
			new DirectoryWalker<T>(res, testWalker, outFileExtension, resultHolder);
		
		try {
			new ProgressMonitorDialog(new Shell()).run(false, false, dirWalker);
		}
		catch (Exception e) {
			ErrorHandler.logAndRethrow("Error occured when exporting file(s)", e);
		}
	}
	
	
	
	public static void exportWithCustomDirectoryWalker(IRunnableWithProgress directoryWalker) {
		
		try {
			new ProgressMonitorDialog(new Shell()).run(false, false, directoryWalker);
		}
		catch (Exception e) {
			ErrorHandler.logAndRethrow("Error occured when exporting file(s)", e);
		}
	}
}
