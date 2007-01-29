package org.cubictest.ui.gef.interfaces;

public interface IDisposeSubject {
	public void addDisposeListener(IDisposeListener listener);
	
	public void removeDisposeListener(IDisposeListener listener);
}
