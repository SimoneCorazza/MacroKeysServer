package com.macrokeysserver.gui;

import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.macrokeys.MacroSetup;
import com.macrokeysserver.option.Options;


/**
 * Regroup the {@link JFileChooser} used for the loading/saving of something
 */
public final class FileChoosers {
	
	private FileChoosers() {
		
	}
	
	
	/**
	 * Open a {@link JFileChooser} for the selecion
	 * of a {@link MacroSetup}
	 * @param parent Fether of the {@link JFileChooser}; null if none
	 * @return Path selected by the user; is null if the user cancel the operation
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
