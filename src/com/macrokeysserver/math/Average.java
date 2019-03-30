package com.macrokeysserver.math;

/** Class to calculate the averege of {@link int} */
public class Average {
	
	private int c = 0;
	private long sum = 0;
	private int avg = 0;
	
	
	public Average() {
		
	}
	
	/**
	 * Add a value to the average
	 * @param i Value to add
	 */
	public void add(int i) {
		sum += i;
		c++;
		avg = (int)(sum / (long)c);
	}
	
	/**
	 * @return Average
	 */
	public int average() {
		return avg;
	}
}
