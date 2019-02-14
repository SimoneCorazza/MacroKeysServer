package com.macrokeysserver.components;

import org.eclipse.jdt.annotation.NonNull;

/** Listener per l'evento della modifica di un attributo */
public interface ActionPerformedListener {
	/**
	 * Mutamento dell'attributo indicato 
	 * @param name Nome dell'attributo (rispettivo del getter e setter)
	 * @param newValue Nuovo valore da assegnare
	 */
	void action(@NonNull String name, Object newValue);
}
