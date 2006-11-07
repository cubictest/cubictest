/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.resources.interfaces;

import org.eclipse.core.resources.IResource;

public interface IResourceListener {
	public void notifyResourceChange(IResource resource);
}
