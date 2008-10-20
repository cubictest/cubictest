package org.cubictest.model;

import java.beans.PropertyChangeListener;

public interface NamePropertyObject {

	void setName(String newName);

	String getName();

	void removePropertyChangeListener(
			PropertyChangeListener abstractPageListener);

	void addPropertyChangeListener(PropertyChangeListener abstractPageListener);

	
}
