package com.macrokeysserver.option;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


/**
 * Coda gestica con politica LRU.
 * <p>
 * Le istanze vengono messe in una coda, se la coda supera il numero massimo
 * l'elemento in fondo alla coda (che è quello non usato da più tempo) viene rimosso.
 * </p>
 * <p>
 * Se un'istanza è già presente (usato {@link #equals(Object)} come metodo
 * di confronto) allora viene portata in testa alla coda.
 * </p>
 */
public class LRUQueque<T> {
	
	/** Coda delle istanze; in testa ci sono gli elementi più recenti, in coda quelli più vecchi */
	private List<T> obj = new LinkedList<>();
	
	/** Limite massimo della coda */
	private int limit;
	
	
	/**
	 * 
	 * @param limit Limite massimo della cronologia; > 0
	 */
	public LRUQueque(int limit) {
		setLimit(limit);
	}
	
	
	/**
	 * @return Lista di oggetti attualmente nella coda; lista non modificabile
	 */
	public List<T> getObjects() {
		return Collections.unmodifiableList(obj);
	}
	
	
	/**
	 * @return Limite della cronologia
	 */
	public int getLimit() {
		return this.limit;
	}
	
	
	/**
	 * Imposta il limite della cronologia
	 * @param limit Limite
	 * @throws IllegalArgumentException Se {@code limit} è <= 0
	 */
	public void setLimit(int limit) {
		if(limit <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.limit = limit;
	}
	
	
	/**
	 * Aggiunge un item recente
	 * @param item Item da aggiungere
	 * @throws NullPointerException Se l'item è null
	 */
	public void add(T item) {
		if(item == null) {
			throw new NullPointerException();
		}
		assert limit > 0;
		
		
		int i = contains(item);
		if(i == -1) {
			obj.add(0, item);
			
			if(obj.size() > limit) {
				obj.remove(obj.size() - 1);
			}
		} else {
			obj.remove(i);
			obj.add(0, item);
		}
	}
	
	
	/**
	 * Aggiunge tutti gli item nella collezione
	 * @param c Collezione da aggiungere
	 * @throws NullPointerException Se {@code c} è null
	 */
	public void addAll(Collection<T> c) {
		Objects.requireNonNull(c);
		
		for(T i : c) {
			add(i);
		}
	}
	
	
	/**
	 * Rimuove tutti gli elementi nella coda
	 */
	public void clear() {
		obj.clear();
	}
	
	
	/**
	 * Indica se l'elemento è presente nella cronologia
	 * @param item Elemento da cercare
	 * @return Posizione in {@link #obj} di {@code item}; se non trovato -1
	 */
	private int contains(T item) {
		assert item != null;
		
		int i = 0;
		for(T t : obj) {
			if(t.equals(item)) {
				return i;
			}
			i++;
		}
		
		return -1;
	}
}
