package com.macrokeysserver;

import java.awt.AWTException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroSetup;
import com.macrokeys.comunication.MacroServer;
import com.macrokeys.netcode.MacroNetServer;
import com.macrokeysserver.bluetooth.MacroBluetoothServer;

/**
 * Permette di controllare le varie istanze di {@link MacroServer}
 */
public class MacroServerManager {
	
	private final EventListenerList listeners = new EventListenerList();
	
	/** Setup attualmente utilizzata; inizialmente null */
	private MacroSetup setup;
	
	
	
	/**
	 * Elenco di server attualmente presenti; in qualsiasi istante ci può
	 * essere solo un server per ogni tipologia
	 */
	private final Map<ServiceType, MacroServer> servers = new HashMap<>();
	
	
	
	
	public MacroServerManager() {
		
	}
	
	
	
	
	
	
	/**
	 * Aggiunge un listener per gli eventi
	 * @param l Evento da aggiungere; non null
	 */
	public void addListener(ServerManagerListener l) {
		Objects.requireNonNull(l);
		
		listeners.add(ServerManagerListener.class, l);
		// Aggiungo l'evento ai server
		for(MacroServer s : servers()) {
			s.addEventListener(l);
		}
	}
	
	
	
	
	/**
	 * Rimuove un listener per gli eventi
	 * @param l Evento da rimuovere; non null
	 */
	public void removeListener(ServerManagerListener l) {
		Objects.requireNonNull(l);
		
		listeners.remove(ServerManagerListener.class, l);
		// Rimuovo l'evento dai server
		for(MacroServer s : servers()) {
			s.removeEventListener(l);
		}
	}
	
	
	
	/**
	 * @return Lista contenente tutti i server attivi
	 */
	public List<MacroServer> servers() {
		return new ArrayList<>(servers.values());
	}
	
	
	/**
	 * @return True se c'è almento un server, False altriemnti
	 */
	public boolean isAtLeastOneServerPresent() {
		return servers.size() > 0;
	}
	
	
	/**
	 * Permette di cambiare la {@link MacroSetup} che verrà usata da tutti i
	 * {@link MacroServer} presenti e futuri.
	 * <p>E' necessario chiamare questo metodo prima di creare un qualsiasi
	 * {@link MacroServer}
	 * </p>
	 * @param setup Nuova setup da utilizzare; non null
	 * @throws NullPointerException se {@code setup} è null
	 */
	public void changeMacroSetup(@NonNull MacroSetup setup) {
		Objects.requireNonNull(setup);
		this.setup = setup;
		
		for(MacroServer s : servers.values()) {
			s.changeMacroSetup(setup);
		}
	}
	
	
	/**
	 * @return {@link MacroSetup} attualmente utilizzata; null se mai impostata
	 */
	public MacroSetup getMacroSetup() {
		return setup;
	}
	
	
	
	/**
	 * Imposta il server per le connessioni TCP/IP.
	 * Il veccho server viene chiuso.
	 * <p>
	 * La {@link MacroSetup} inizialmente utilizzata equivale a {@link #getMacroSetup()}.
	 * </p>
	 * <p>
	 * Prima di chiamare questo metodo è bene che la {@link MacroSetup} venga settata tramite
	 * {@link #getMacroSetup()}
	 * </p>
	 * @throws AWTException Se c'è un problema con l'inizializzazione di
	 * {@link Robot}
	 * @throws IOException Se c'è un problema nella creazione del socket del server
	 * @throws IllegalStateException Se la {@link MacroSetup} non è stata settata
	 * @see MacroNetServer#MacroNetServer(int, String, MacroSetup)
	 * @see #changeMacroSetup(MacroSetup)
	 */
	public void macroNetServer()
			throws IOException, AWTException {
		if(getMacroSetup() == null) {
			throw new IllegalStateException("Macro setup not set");
		}
		
		closeMacroServer(ServiceType.TCP_IP);
		
		MacroNetServer serverNet = new MacroNetServer(getMacroSetup());
		serverNet.start();
		
		commonProcedureAddMacroServer(ServiceType.TCP_IP, serverNet);
	}
	
	
	
	/**
	 * Imposta il server per le connessioni Bluetooth.
	 * Il veccho server, se presente, viene chiuso.
	 * <p>
	 * La {@link MacroSetup} inizialmente utilizzata equivale a {@link #getMacroSetup()}.
	 * </p>
	 * <p>
	 * Prima di chiamare questo metodo è bene che la {@link MacroSetup} venga settata tramite
	 * {@link #getMacroSetup()}
	 * </p>
	 * @throws IOException Se c'è un problema nell'inizializzazione del server
	 * @throws AWTException Se c'è un problema con l'inizializzazione di
	 * {@link Robot}
	 * @throws IllegalStateException Se la {@link MacroSetup} non è stata settata
	 * @see MacroBluetoothServer#MacroBluetoothServer(MacroSetup)
	 * @see #changeMacroSetup(MacroSetup)
	 */
	public void macroBluetoothServer()
			throws IOException, AWTException {
		if(getMacroSetup() == null) {
			throw new IllegalStateException("Macro setup not set");
		}
		
		closeMacroServer(ServiceType.Bluetooth);
		
		MacroBluetoothServer serverBluetooth = new MacroBluetoothServer(setup);
		serverBluetooth.start();
		
		commonProcedureAddMacroServer(ServiceType.Bluetooth, serverBluetooth);
	}
	
	
	
	/**
	 * Esegue le operazioni comuni all'aggiunta di un nuovo server:
	 * <li>Aggiunge il server all'elenco dei server
	 * <li>Aggiungere gli eventi {@link #listeners} al nuovo {@link MacroServer} {@code s}
	 * <li>Generare l'evento di aggiunta di un server
	 * @param t Tipologia del server
	 * @param s Server da aggiungere
	 */
	private void commonProcedureAddMacroServer(@NonNull ServiceType t,
			@NonNull MacroServer s) {
		servers.put(t, s);
		
		// Aggiungo gli eventi
		for(ServerManagerListener l : 
			listeners.getListeners(ServerManagerListener.class)) {
			s.addEventListener(l);
		}
		
		// Genero l'evento di aggiunta del server
		fireServerStart(s);
	}
	
	
	
	/**
	 * Chiude il server indicato, se presente
	 * @param s Tipologia di server da chiudere
	 */
	public void closeMacroServer(@NonNull ServiceType s) {
		MacroServer server = servers.get(s);
		if(server != null) {
			// MacroServer.close() genera il relativo evento e this, per
			// coerenza, deve mostrare che il server non è più contenuto
			servers.remove(s);
			server.close();
			// Rimuovo gli eventi associati
			for(MacroServer.EventListener l : 
				listeners.getListeners(ServerManagerListener.class)) {
				server.removeEventListener(l);
			}
		}
	}
	
	
	
	/**
	 * Chiude tutti i server attualmente attivi
	 * @see #closeMacroServer(ServiceType)
	 */
	public void closeAllServers() {
		// Clono il set siccome verrà poi modificato nel foreach
		Set<ServiceType> se = new HashSet<>(servers.keySet());
		for(ServiceType t : se) {
			closeMacroServer(t);
		}
	}
	
	
	/**
	 * @param s Tipologia di server da verificare la presenza
	 * @return True se il server che offre la connessione indicata è presente,
	 * False altriemnti
	 */
	public boolean isMacroServerPresent(@NonNull ServiceType s) {
		return servers.containsKey(s);
	}
	
	
	
	/**
	 * Genera l'evento di creazione di un server
	 * @param server Server creato; non null
	 */
	private void fireServerStart(MacroServer server) {
		assert server != null;
		
		for(ServerManagerListener l : 
			listeners.getListeners(ServerManagerListener.class)) {
			l.onStart(server);
		}
	}
	
	
	/**
	 * @param s Tipologia di server desiderato
	 * @return Server indicato; null se non presente
	 */
	public MacroServer getMacroServer(@NonNull ServiceType s) {
		return servers.get(s);
	}
	
	
	
	/**
	 * Ottiene la tipologia di servizio offerto dal server
	 * @param server Server il cui servizio offerto è da scoprire
	 * @return Servizio offerto dal server; non null
	 * @throws IllegalArgumentException Se {@code server} non rientra tra le tipologie di server
	 * @throws NullPointerException Se {@code server} è null
	 */
	public static ServiceType serverService(MacroServer server) {
		Objects.requireNonNull(server);
		
		if(server instanceof MacroNetServer) {
			return ServiceType.TCP_IP;
		} else if(server instanceof MacroBluetoothServer) {
			return ServiceType.Bluetooth;
		} else {
			throw new IllegalArgumentException("Server service not found");
		}
	}
	
	
	
	/**
	 * Listener per gli eventi relativi ad un {@link MacroServer}
	 */
	public interface ServerManagerListener extends EventListener, MacroServer.EventListener {
		
		/**
		 * Alla creazione di un nuovo server
		 * @param server Server creato; non null
		 */
		public void onStart(MacroServer server);
	}
}
