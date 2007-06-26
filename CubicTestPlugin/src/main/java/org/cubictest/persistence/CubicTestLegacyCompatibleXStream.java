package org.cubictest.persistence;

/**
 * XStream subclass that adds support for reading old test files.
 * I.e. maps old elements to new classes.
 * Not feasible to use for objects in collections, as they will have 
 * the wrong name in the setter method in the class.
 * 
 * @author chr_schwarz
 */
public class CubicTestLegacyCompatibleXStream extends CubicTestXStream {

	
}
