//package com.macrokeysserver.actions;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.Stack;
//
//import org.eclipse.jdt.annotation.NonNull;
//
///** Permette di memorizzare e gestire le azioni effettuate */
//public final class ActionManager<T extends Cloneable> {
//	
//	private final T initialState;
//	private final Stack<T> undo = new Stack<>();
//	private final Stack<T> redo = new Stack<>();
//	
//	/**
//	 * @param initialState Stato iniziale oltre al qule non si può più retrocedere; può essere null
//	 * @throws CloneException Se c'è un problema di clonazione
//	 */
//	public ActionManager(T initialState) throws CloneException {
//		if(initialState == null) {
//			this.initialState = null;
//		} else {
//			this.initialState = invokeClone(initialState);
//		}
//	}
//	
//	/**
//	 * Memorizza lo stato dell'oggetto
//	 * <p>Si suppone l'implementazione corretta di clone</p>
//	 * @param a Stato da memorizzare
//	 * @throws CloneException Se l'oggetto {@code a} non implementa il metodo clone
//	 * o in un qualunque modo non è possibile utilizzarlo
//	 */
//	public void addState(@NonNull T a) throws CloneException {
//		T t;
//		//Forzo la chiamata di clone
//		t = invokeClone(a);
//		
//		redo.clear();
//		undo.push(t);
//	}
//	
//	/**
//	 * Permette di tornare allo stato precedente
//	 * @return Stato precedente; se non ci sono stati precedenti ritorna lo stato
//	 * iniziale, che può essere null
//	 * @throws CloneException Se c'è un problema di clonazione
//	 */
//	public T undo() throws CloneException {
//		T r;
//		if(undo.isEmpty()) {
//			r = initialState;
//		} else {
//			r = undo.pop();
//			redo.push(r);
//		}
//		
//		return invokeClone(r);
//	}
//	
//	/**
//	 * Permette di tornare allo stato precedente alla scorsa operazione di undo
//	 * @return Stato successivo;
//	 * @throws CloneException Se c'è un problema di clonazione
//	 */
//	public T redo() throws CloneException {
//		T r;
//		if(redo.isEmpty()) {
//			if(undo.isEmpty()) {
//				r = initialState;
//			} else {
//				r = undo.peek();
//			}
//		} else {
//			r = redo.pop();
//			undo.push(r);
//		}
//		
//		return invokeClone(r);
//	}
//	
//	/**
//	 * Esegue im metodo clone dell'oggetto indicato
//	 * <p>Si suppone l'implementazione corretta di clone</p>
//	 * @param o Oggetto da clonare
//	 * @throws CloneException Se l'oggetto {@code o} non implementa il metodo clone
//	 * o in un qualunque modo non è possibile utilizzarlo
//	 */
//	private static <T> T invokeClone(T o) throws CloneException {
//		try {
//			return (T)o.getClass().getMethod("clone").invoke(o);
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
//				| SecurityException | ClassCastException e) {
//			throw new CloneException(e);
//		}
//	}
//}
