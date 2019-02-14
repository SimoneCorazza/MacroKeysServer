package com.macrokeysserver.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/** Classe statica per utilità di IO */
public final class IOUtil {

	private IOUtil() {}
	
	/**
	 * Carica un file di testo
	 * @param path - Percorso del file di testo
	 * @param charset - Set di caratteri da utilizzare per la decodifica {@link StandardCharsets}
	 * @return File di testo caricato; aggiungendo un "\n" in più alla fine
	 * @throws IOException - Se si verifica un errore di IO
	 * @see StandardCharsets
	 */
	public static String loadAsTextFile(String path, Charset charset) throws IOException {
		Objects.requireNonNull(path);
		Objects.requireNonNull(charset);
		
		List<String> lines = Files.readAllLines(Paths.get(path), charset);
		StringBuilder sb = new StringBuilder();
		if(charset.equals(StandardCharsets.UTF_8) && lines.size() > 0) {
			String n = removeUTF8BOM(lines.get(0));
			lines.remove(0);
			lines.add(0, n);
		}
		for(String l : lines) {
			sb.append(l);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static final String UTF8_BOM = "\uFEFF";

	private static String removeUTF8BOM(String s) {
	    if (s.startsWith(UTF8_BOM)) {
	        s = s.substring(1);
	    }
	    return s;
	}
	
	/**
	 * @param f - File da cui estrarre l'estensione
	 * @return Estensione del file; null se non presente
	 */
    public static String getExtension(File f) {
    	Objects.requireNonNull(f);
    	
        String s = f.getName();
        return getExtension(s);
    }
    
    /**
	 * @param p - Path del file
	 * @return Estensione del file; null se non presente
	 */
    public static String getExtension(String p) {
    	Objects.requireNonNull(p);
    	
        int i = p.lastIndexOf('.');

        if (i > 0 &&  i < p.length() - 1) {
            return p.substring(i + 1).toLowerCase();
        } else {
        	return null;
        }
    }
}
