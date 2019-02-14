package com.macrokeysserver.math;

/** Classe per fare la media matematica di valori {@link int} */
public class Average {
	
	private int c = 0;
	private long sum = 0;
	private int avg = 0;
	
	
	public Average() {
		
	}
	
	/**
	 * Aggiunge un valore alla media
	 * @param i Valore da aggiungere
	 */
	public void add(int i) {
		sum += i;
		c++;
		avg = (int)(sum / (long)c);
	}
	
	/**
	 * @return Media
	 */
	public int average() {
		return avg;
	}
}
