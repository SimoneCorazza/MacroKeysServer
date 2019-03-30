package com.macrokeysserver.option;


/**
 * Listener for changes at the options
 */
public interface OptionListener {
	
	/**
	 * Callback called at the insertion af a path in the macroSetup used recentlly
	 * @param path Added path
	 */
	void macroSetupPathAdded(String path);
	
}
