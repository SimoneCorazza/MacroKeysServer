package com.macrokeysserver.option;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;


/**
 * QUeque with LRU policy
 * <p>
 * Items are placed inside a queque if the length of the queque excedes
 * {@ #getLimit()} the older item is removed.
 * </p>
 * <p>
 * If an item is already present ({@link #equals(Object)} is used to
 * compare them) the item is not added to the queque, but the item
 * that is already inside the queque is placed on the head of the queque.
 * </p>
 */
public class LRUQueque<T> {
	
	/** Queque of the items; in the head there are the most recent added elements in the tail therea are the oldest */
	private List<T> obj = new LinkedList<>();
	
	/** Limit of the queque */
	private int limit;
	
	
	/**
	 * @param limit Limit of the queque; > 0
	 * @throws IllegalArgumentException If {@code limit} is <= 0
	 */
	public LRUQueque(int limit) {
		setLimit(limit);
	}
	
	
	/**
	 * @return List of the items in the queque; this list is not mutable
	 */
	public List<T> getObjects() {
		return Collections.unmodifiableList(obj);
	}
	
	
	/**
	 * @return Limit of the size of the queque
	 */
	public int getLimit() {
		return this.limit;
	}
	
	
	/**
	 * Set the limit of the queque
	 * @param limit Limit of the size of the queque
	 * @throws IllegalArgumentException If {@code limit} is <= 0
	 */
	public void setLimit(int limit) {
		if(limit <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.limit = limit;
	}
	
	
	/**
	 * Add an item to the head of the queque
	 * @param item item to add
	 */
	public void add(@NonNull T item) {
		Objects.requireNonNull(item);
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
	 * Adds the items in the queque
	 * @param c Items to add
	 */
	public void addAll(@NonNull Collection<T> c) {
		Objects.requireNonNull(c);
		
		for(T i : c) {
			add(i);
		}
	}
	
	
	/**
	 * Remove all items from the queque
	 */
	public void clear() {
		obj.clear();
	}
	
	
	/**
	 * Check if the given item is present in the queque
	 * @param item Item to search
	 * @return Position in {@link #obj} of {@code item}; if not found return -1
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
