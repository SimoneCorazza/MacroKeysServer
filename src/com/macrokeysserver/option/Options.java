package com.macrokeysserver.option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.macrokeys.MacroSetup;



/**
 * Classe statica che permette la gestione delle opzioni.
 * <p>
 * Consente di gestire, caricare e salavere permanentemente le opzioni.
 * I metodi {@link #load()} e {@link #save()} per il salvataggio e il caricamento delle opzioni.
 * </p>
 */
public final class Options {
	
	private static final String MACRO_SETUP_RECENT_FILES = "MACRO_SETUP_RECENT_FILES";

	private static final OptionManager pref = new OptionManager("options");
	
	private static final List<OptionListener> lis = new ArrayList<>();
	
	
	/** Lista di path recenti relative alle {@link MacroSetup} recentemente caricate */
	public static final LRUQueque<String> macroSetupsFiles = new LRUQueque<>(15);
	
	
	
	private Options() { }
	
	

	/**
	 * Carica le impostazioni dal file
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
	 * Salva tutte le preferenze
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
	 * Aggiungi una path per una {@link MacroSetup} caricata
	 * @param path Path
	 * @throws IllegalArgumentException Se {@code path} è null o vuota
	 */
	public static void addMacroSetupPath(String path) {
		if(path == null || path.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		macroSetupsFiles.add(path);
		
		fireMacroSetupPathAdded(path);
	}
	
	
	
	
	/**
	 * Aggiunge un listener
	 * @param l Listener da aggiungere
	 */
	public static void addEventListener(OptionListener l) {
		if(l == null) {
			throw new NullPointerException();
		}
		
		lis.add(l);
	}
	
	
	/**
	 * Rimuove il listener indicato se presente
	 * @param l Listener da rimuovere
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
