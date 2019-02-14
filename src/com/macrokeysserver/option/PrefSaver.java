package com.macrokeysserver.option;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Per salvare un elemento nelle preferenze
 * @param <T>
 */
interface PrefSaver<T> {

	/**
	 * Salva l'elemento nelle preferenze
	 * @param item Item da salvare
	 * @param str Stream nel quale salvarlo
	 * @throws IOException Nel caso di errore di IO
	 */
	void save(T item, DataOutputStream str) throws IOException;
	
}
