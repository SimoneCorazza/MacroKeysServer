package com.macrokeysserver.option;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Listenere per il caricamento di un'istanza di una classe da stream
 * @param <T> Tipologia di classe da caricare
 */
interface PrefLoader<T> {
	
	/**
	 * Carica un istanza della classe dallo stread
	 * @param str Stream dal quale ottenere i dati
	 * @return Istanza caricata
	 * @throws IOException Se c'è un errore di IO
	 */
	T load(DataInputStream str) throws IOException;
	
}
