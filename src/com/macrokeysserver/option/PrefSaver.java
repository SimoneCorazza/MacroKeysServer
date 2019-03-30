package com.macrokeysserver.option;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Interface to save an element in the preferences
 */
interface PrefSaver<T> {

	/**
	 * Save the element in the preferences
	 * @param item Item to save
	 * @param str Stream where to save the element
	 * @throws IOException In case of IO error
	 */
	void save(T item, DataOutputStream str) throws IOException;
	
}
