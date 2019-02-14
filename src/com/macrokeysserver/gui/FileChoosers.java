package com.macrokeysserver.gui;

import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.macrokeys.MacroSetup;
import com.macrokeysserver.option.Options;


/**
 * Raggruppa i {@link JFileChooser} utilizzati per il caricamento
 * o salvataggio di qualcosa
 */
public final class FileChoosers {
	
	private FileChoosers() {
		
	}
	
	
	/**
	 * Apre una {@link JFileChooser} che consente all'utente di
	 * selezionare una {@link MacroSetup}
	 * @param parent Il componente padre della {@link JFileChooser};
	 * può essere null
	 * @return Path selezionata dall'utente; null se l'utente annulla
	 * l'operazione
	 * @throws NullPointerException Se {@code parent} è null
	 * @see JFileChooser#showOpenDialog(Component)
	 */
	public static String macroSetup(Component parent) {
		JFileChooser f = new JFileChooser();
		f.setFileFilter(new FileNameExtensionFilter("File contenenti le macro (." + 
				FileExtensions.MACRO_SETUP + ")",
				FileExtensions.MACRO_SETUP
				));
		
		// Lista di path recentemente usate
		List<String> paths = Options.macroSetupsFiles.getObjects();
		// Considero la path più recente nella cronologia, se presente
		String path = paths.size() == 0 ? System.getProperty("user.dir") : paths.get(0);
		
		f.setCurrentDirectory(new File(path));
		
		boolean sel = f.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION;
		if(sel) {
			return f.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}
}
