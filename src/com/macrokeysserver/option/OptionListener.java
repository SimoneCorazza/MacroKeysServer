package com.macrokeysserver.option;


/**
 * Listener ai cambiamenti delle opzioni
 */
public interface OptionListener {
	
	/**
	 * Se una path è stata aggiunta nell'elenco dell macroSetup recenti
	 * @param path Path aggiunta
	 */
	void macroSetupPathAdded(String path);
	
}
