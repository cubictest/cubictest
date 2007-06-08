/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.factory;

import org.cubictest.model.SubTest;

public class SubTestFactory extends FileFactory {
	
	public Object getNewObject() {
		SubTest subTest = new SubTest(file.getProjectRelativePath().toPortableString(), file.getProject());
		return subTest;
	}

}
