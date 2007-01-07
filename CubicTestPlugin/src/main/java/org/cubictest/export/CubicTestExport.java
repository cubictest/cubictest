package org.cubictest.export;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.exception.CubicException;
import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.export.converters.TreeTestWalker;
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

	public static <T> void exportSelection(IStructuredSelection selection,
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
			e.printStackTrace();
			throw new CubicException("Error when exporting file(s): " + e.toString(), e);
		}
	}
}
