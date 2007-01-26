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

/**
 * Util class for handling exceptions (show error message to user, log error, etc.)
 * 
 * @author chr_schwarz
 *
 */
public class ExceptionHandler {

	public static void rethrow(Exception e) {
		e.printStackTrace();
		
		throw new CubicException(e.toString(), e);
	}

	public static void rethrow(Exception e, String message) {
		e.printStackTrace();
		
		if (StringUtils.isBlank(message)) {
			throw new CubicException(e.toString(), e);
		} else {
			throw new CubicException(message, e);
		}
	}

	public static void handleException(Exception e) {
		Shell shell = new Shell();
		e.printStackTrace();
	
		if(e instanceof InterruptedException) {
			MessageDialog.openError(shell, UiText.APP_TITLE, UiText.INTERRUPTED_MESSAGE);
		}
		else {
			MessageDialog.openError(shell, UiText.APP_TITLE, UiText.GENERAL_ERROR_MESSAGE + "\n"
					+ e.toString());
		}

	}

}
