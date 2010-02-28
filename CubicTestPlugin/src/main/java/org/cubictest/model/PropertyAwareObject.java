/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.cubictest.common.utils.CubicCloner;

/**
 * Base class for all model objects. Contains publish subscribe logic.
 * 
 * @author SK Skytteren
 */
public abstract class PropertyAwareObject implements Cloneable {
	public static final String CHILD = "CHILD";
	public static final String REORDER = "REORDER";
	public static final String BOUNDS = "BOUNDS";
	public static final String INPUT = "INPUT";
	public static final String OUTPUT = "OUTPUT";
	public static final String NAME = "NAME";
	public static final String VALUE = "VALUE";
	public static final String LAYOUT = "LAYOUT";
	public static final String STATUS = "STATUS";
	public static final String PARAM = "PARAM";
	public static final String NOT = "NOT";
	public static final String CUSTOMSTEP = "CUSTOMSTEP";
	public static final String PARENT = "PARENT";
	
	protected TestPartStatus status;
	protected transient StandardToStringStyle toStringStyle;
	
	private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	protected PropertyAwareObject(){
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl){
		getListeners().addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl){
		getListeners().removePropertyChangeListener(pcl);
	}
	
	protected void firePropertyChange(String prop, Object old, Object newValue){
		PropertyChangeSupport list = getListeners();
		list.firePropertyChange(prop, old, newValue);
	}	

	public TestPartStatus getStatus(){
		return this.status;
	}
	
	public void setStatus(TestPartStatus newStatus) {
		TestPartStatus old = status;
		status = newStatus;
		firePropertyChange(PropertyAwareObject.STATUS, old, newStatus);
	}
	
	public abstract void resetStatus();

	protected void setListeners(PropertyChangeSupport listeners) {
		this.listeners = listeners;
	}

	protected PropertyChangeSupport getListeners() {
		if (listeners == null)
			listeners = new PropertyChangeSupport(this);
		return listeners;
	}
	
	@Override
	public PropertyAwareObject clone() throws CloneNotSupportedException {
		return (PropertyAwareObject) CubicCloner.deepCopy(this);
	}
	
	public boolean isEqualTo(Object other) {
		if (toStringStyle == null) {
			toStringStyle = new StandardToStringStyle();
		}
		toStringStyle.setUseIdentityHashCode(false);
		return ToStringBuilder.reflectionToString(other, toStringStyle).equals(ToStringBuilder.reflectionToString(this, toStringStyle));
	}
	
	/** 
	 * Get the name of the element used for visual identification.
	 * Does not have to be unique.
	 * Should be overridden by subclasses
	 */
	public String getName() {
		return getClass().getSimpleName();
	}

}
