/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author trond
 *
 */
public abstract class TransitionNode extends PropertyAwareObject{
	private Transition inTransition = null;
	private List<Transition> outTransitions = new ArrayList<Transition>();
	private String id = "";
;
	private Point position;
	private Dimension dimension;
	private String name = "";
	private boolean autoPosition = false;
	
	public TransitionNode() {
		this.setId(getNewGeneratedId());
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public static Dimension getDefaultDimension() {
		return new Dimension(93, 70);
	}
	
	/**
	 * @return Returns the postition.
	 */
	public Point getPosition() {
		if(position == null) {
			position = new Point(0,0);
		}
		return position;
	}
	/**
	 * @param postition The postition to set.
	 */
	public void setPosition(Point position) {
		Point oldPos = this.position;
		this.position = position;
		firePropertyChange(PropertyAwareObject.LAYOUT,oldPos,position);
	}
	/**
	 * @return
	 */
	public Transition getInTransition() {
		return inTransition;
	}
	/**
	 * @param inTransition The inTransition to set.
	 */
	public void setInTransition(Transition inTransition) {
		Transition oldTransition = this.inTransition;
		this.inTransition = inTransition;
		if (inTransition != null)
			firePropertyChange(PropertyAwareObject.INPUT,oldTransition,inTransition);
	}
	/**
	 * @return Returns the outTransitions.
	 */
	public List<Transition> getOutTransitions() {
		return outTransitions;
	}
	/**
	 * @param outTransitions The outTransitions to set.
	 */
	public void setOutTransitions(List<Transition> outTransitions) {
		this.outTransitions = outTransitions;
	}
	/**
	 * @param transition
	 */
	public void addOutTransition(Transition transition){
		outTransitions.add(transition);
		firePropertyChange(PropertyAwareObject.OUTPUT,null,transition);
	}
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param linkTransition
	 */
	public void removeOutTransition(Transition transition) {
		outTransitions.remove(transition);
		firePropertyChange(PropertyAwareObject.OUTPUT,transition,null);
	}
	/**
	 * @param linkTransition
	 */
	public void removeInTransition() {
		Transition oldTrans = inTransition;
		inTransition = null;
		firePropertyChange(PropertyAwareObject.INPUT,oldTrans,null);
		
	}
	
	@Override
	protected TransitionNode clone() throws CloneNotSupportedException {
		TransitionNode clone = (TransitionNode)super.clone();
		clone.setId(getNewGeneratedId());
		clone.inTransition = null;
		clone.outTransitions = new ArrayList<Transition>();
		return clone;
	}
	
	public Dimension getDimension() {
		if(dimension == null) {
			dimension = getDefaultDimension();
		}
		return dimension;
	}
	
	public void setDimension(Dimension dimension) {
		Dimension oldDim = this.dimension;
		this.dimension = dimension;
		firePropertyChange(PropertyAwareObject.LAYOUT, oldDim, dimension);
	}

	private String getNewGeneratedId() {
		String name = getClass().getName();
		name = name.substring(name.lastIndexOf(".") + 1);
		name = name.toLowerCase();
		return name + this.hashCode() + System.currentTimeMillis();
	}

	public void setAutoPosition(boolean autoPosition) {
		this.autoPosition = autoPosition;
	}

	public boolean getAutoPosition() {
		return autoPosition;
	}
	
	public boolean hasInTransition() {
		return getInTransition() != null;
	}

}