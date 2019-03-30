package com.macrokeysserver.option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.macrokeys.MacroSetup;



/**
 * Static class to manage the persistence of options
 */
public final class Options {
	
	private static final String MACRO_SETUP_RECENT_FILES = "MACRO_SETUP_RECENT_FILES";

	private static final OptionManager pref = new OptionManager("options");
	
	private static final List<OptionListener> lis = new ArrayList<>();
	
	
	/** Paths of the recent loaded {@link MacroSetup} */
	public static final LRUQueque<String> macroSetupsFiles = new LRUQueque<>(15);
	
	
	
	private Options() { }
	
	

	/**
	 * Load setting from the file
	 */
	public static void load() {
		try {
			pref.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> msfiles = pref.get(MACRO_SETUP_RECENT_FILES,
				new ArrayList<>());
		macroSetupsFiles.addAll(msfiles);
	}
	

	
	
	/**
	 * Save all settings
	 */
	public static void save() {
		pref.put(MACRO_SETUP_RECENT_FILES, macroSetupsFiles.getObjects());
		
		try {
			pref.save();			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Adds a path for a loaded {@link MacroSetup}
	 * @param path Path
	 * @throws IllegalArgumentException If {@code path} is empty
	 */
	public static void addMacroSetupPath(String path) {
		Objects.requireNonNull(path);
		if(path.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		macroSetupsFiles.add(path);
		
		fireMacroSetupPathAdded(path);
	}
	
	
	
	
	/**
	 * Adds a listener
	 * @param l Listener to add
	 */
	public static void addEventListener(OptionListener l) {
		if(l == null) {
			throw new NullPointerException();
		}
		
		lis.add(l);
	}
	
	
	/**
	 * Remove the listener
	 * @param l Listener to remove
	 */
	public static void removeEventLIstener(OptionListener l) {
		Iterator<OptionListener> it = lis.iterator();
		while(it.hasNext()) {
			if (it.next() == l) {
				it.remove();
			}
		}
	}
	
	
	private static void fireMacroSetupPathAdded(String path) {
		assert path != null;
		
		for(OptionListener l : lis) {
			l.macroSetupPathAdded(path);
		}
	}
	
}
