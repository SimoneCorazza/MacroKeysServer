package com.macrokeysserver.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

/** Static class for IO utilities */
public final class IOUtil {

	private IOUtil() {}
	
	/**
	 * Load a text file
	 * @param path Path of the text file
	 * @param charset Charset for the decoding {@link StandardCharsets}
	 * @return Content of the text file; an "\n" is added at the end of the file
	 * @throws IOException In case of IO error
	 * @see StandardCharsets
	 */
	public static String loadAsTextFile(@NonNull String path,
			@NonNull Charset charset) throws IOException {
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
	 * Get the extension of a file
	 * @param f File from witch get the extension
	 * @return File extension; null if not present
	 */
    public static String getExtension(@NonNull File f) {
    	Objects.requireNonNull(f);
    	
        String s = f.getName();
        return getExtension(s);
    }
    
    /**
     * Get the extension of a file
	 * @param p Fil path
	 * @return File extension; null if not present
	 */
    public static String getExtension(@NonNull String p) {
    	Objects.requireNonNull(p);
    	
        int i = p.lastIndexOf('.');

        if (i > 0 &&  i < p.length() - 1) {
            return p.substring(i + 1).toLowerCase();
        } else {
        	return null;
        }
    }
}
