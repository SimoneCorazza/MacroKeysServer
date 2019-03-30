package com.macrokeysserver.components;

import org.eclipse.jdt.annotation.NonNull;

/** Listener for the edit event of an attribute of an {@code object} */
public interface ActionPerformedListener {
	
	/**
	 * Callback of the mutation of an attibute of an object
	 * @param name Name of the attribute (based on getter and setter)
	 * @param newValue New value to set
	 */
	void action(@NonNull String name, Object newValue);
}
