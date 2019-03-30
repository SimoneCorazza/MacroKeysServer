package com.macrokeysserver.option;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Interface to load an element of the preferences
 * @param <T> Type of the class to load
 */
interface PrefLoader<T> {
	
	/**
	 * Load the element from the stream
	 * @param str Stream from which load the object of the preference
	 * @return Loaded object
	 * @throws IOException In case of IO error
	 */
	T load(DataInputStream str) throws IOException;
	
}
