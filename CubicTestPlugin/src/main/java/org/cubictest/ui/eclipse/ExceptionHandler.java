/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.eclipse;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.resources.UiText;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class ExceptionHandler {

	public void rethrow(Exception e, String message) {
		if (StringUtils.isBlank(message)) {
			throw new CubicException(e.toString(), e);
		} else {
			throw new CubicException(message, e);
		}
	}

	public static void handleException(Exception e) {
		Shell shell = new Shell();
	
		if (e instanceof InvocationTargetException ) {
			MessageDialog.openError(shell, UiText.APP_TITLE, UiText.GENERAL_ERROR_MESSAGE + "\n"
					+ e.toString());
			e.printStackTrace();
		}
		else if(e instanceof InterruptedException) {
			MessageDialog.openError(shell, UiText.APP_TITLE, UiText.INTERRUPTED_MESSAGE);
			e.printStackTrace();
		}
		else {
			MessageDialog.openError(shell, UiText.APP_TITLE, UiText.GENERAL_ERROR_MESSAGE + "\n"
					+ e.toString());
			e.printStackTrace();
		}

	}

	public static void handleError(Error e) {
		Shell shell = new Shell();
		MessageDialog.openError(shell, UiText.APP_TITLE, UiText.GENERAL_ERROR_MESSAGE + "\n"
				+ e.toString());
		e.printStackTrace();
	}
}
