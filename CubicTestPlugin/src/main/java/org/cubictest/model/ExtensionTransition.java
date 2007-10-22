/*
 * Created on 9.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

/**
 * 
 * Transition from an ExtensionPoint in another Test to a Page/State in current test.
 * 
 * @author Christian Schwarz
 */
public class ExtensionTransition extends Transition {

	private String id; //the source page ID
	
	private transient ExtensionPoint extensionPoint;

	public ExtensionTransition() {}
	
	public ExtensionTransition(TransitionNode sourceNode, TransitionNode targetNode, ExtensionPoint extensionPoint) {
		super(sourceNode, targetNode);
		this.id = extensionPoint.getPageId();
	}
	
	public ExtensionPoint getExtensionPoint() {
		if(extensionPoint == null) {
			for(AbstractPage page : ((SubTest)getStart()).getTest(false).getPages()) {
				if(page.getId() == id) {
					for(Transition t : page.getOutTransitions()) {
						if(t.getEnd() instanceof ExtensionPoint) {
							extensionPoint = (ExtensionPoint) t.getEnd();
						}
					}
				}
			}
		}
		return extensionPoint;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": ExtensionPoint (start) = " + getExtensionPoint() + ", end = " + getEnd();
	}
}
