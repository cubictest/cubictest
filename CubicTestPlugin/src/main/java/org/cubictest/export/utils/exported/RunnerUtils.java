package org.cubictest.export.utils.exported;

import org.cubictest.model.Moderator;

/**
 * Util class for test runners.
 * 
 * @author Christian Schwarz
 *
 */
public class RunnerUtils {
	
	public static boolean pass(String fromTest, String fromActualPage, Moderator moderator) {
		if (moderator.equals(Moderator.BEGIN))
			return fromActualPage.startsWith(fromTest);
		if (moderator.equals(Moderator.CONTAIN))
			return fromActualPage.indexOf(fromTest) >= 0;
		if (moderator.equals(Moderator.END))
			return fromActualPage.endsWith(fromTest);
		if (moderator.equals(Moderator.EQUAL))
			return fromActualPage.equals(fromTest);
		throw new IllegalArgumentException("Unknown Moderator");
	}
}
