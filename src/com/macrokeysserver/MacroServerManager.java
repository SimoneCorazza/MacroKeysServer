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
 * Manager for the instances of {@link MacroServer}
 */
public class MacroServerManager {
	
	private final EventListenerList listeners = new EventListenerList();
	
	/** Setup actually used; initially null */
	private MacroSetup setup;
	
	
	/**
	 * Servers actually present; there can be only one type of service ata a time
	 */
	private final Map<ServiceType, MacroServer> servers = new HashMap<>();
	
	
	
	
	public MacroServerManager() {
		
	}
	
	
	
	
	
	
	/**
	 * Add a listener for the events
	 * @param l Event to add
	 */
	public void addListener(@NonNull ServerManagerListener l) {
		Objects.requireNonNull(l);
		
		listeners.add(ServerManagerListener.class, l);
		// Aggiungo l'evento ai server
		for(MacroServer s : servers()) {
			s.addEventListener(l);
		}
	}
	
	
	
	
	/**
	 * Remove a listener from the events
	 * @param l Event to remove
	 */
	public void removeListener(@NonNull ServerManagerListener l) {
		Objects.requireNonNull(l);
		
		listeners.remove(ServerManagerListener.class, l);
		// Rimuovo l'evento dai server
		for(MacroServer s : servers()) {
			s.removeEventListener(l);
		}
	}
	
	
	
	/**
	 * @return Servers actually active
	 */
	public List<MacroServer> servers() {
		return new ArrayList<>(servers.values());
	}
	
	
	/**
	 * @return True if there is at least one server, false otherwise
	 */
	public boolean isAtLeastOneServerPresent() {
		return servers.size() > 0;
	}
	
	
	/**
	 * Sets the {@link MacroSetup} used by all {@link MacroServer}
	 * <p>Call this method before adding any {@link MacroServer}</p>
	 * @param setup Setup to use
	 */
	public void changeMacroSetup(@NonNull MacroSetup setup) {
		Objects.requireNonNull(setup);
		this.setup = setup;
		
		for(MacroServer s : servers.values()) {
			s.changeMacroSetup(setup);
		}
	}
	
	
	/**
	 * @return {@link MacroSetup} used; null if never set
	 */
	public MacroSetup getMacroSetup() {
		return setup;
	}
	
	
	
	/**
	 * Sets the server for TCP/IP connections
	 * The old server, if present, is closed
	 * <p>
	 * Before calling this method the {@link MacroSetup} it must be set by calling
	 * {@link #changeMacroSetup()}
	 * </p>
	 * @throws AWTException If occur an error initializing {@link Robot}
	 * @throws IOException In case of IO error
	 * @throws IllegalStateException If the {@link MacroSetup} was not set
	 * @see MacroNetServer#MacroNetServer(int, String, MacroSetup)
	 * @see #changeMacroSetup(MacroSetup)
	 */
	public void macroNetServer() throws IOException, AWTException {
		if(getMacroSetup() == null) {
			throw new IllegalStateException("Macro setup not set");
		}
		
		closeMacroServer(ServiceType.TCP_IP);
		
		MacroNetServer serverNet = new MacroNetServer(getMacroSetup());
		serverNet.start();
		
		commonProcedureAddMacroServer(ServiceType.TCP_IP, serverNet);
	}
	
	
	
	/**
	 * Sets the server for Bluetooth connections
	 * The old server, if present, is closed
	 * <p>
	 * Before calling this method the {@link MacroSetup} it must be set by calling
	 * {@link #changeMacroSetup()}
	 * </p>
	 * @throws AWTException If occur an error initializing {@link Robot}
	 * @throws IOException In case of IO error
	 * @throws IllegalStateException If the {@link MacroSetup} was not set
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
	 * Common operations for init a new server:
	 * <li>Adds the server to the list of servers
	 * <li>Adds the events {@link #listeners} at the new {@link MacroServer} {@code s}
	 * <li>Generates the event of adding the new server
	 * @param t Type of server
	 * @param s Server to add
	 */
	private void commonProcedureAddMacroServer(@NonNull ServiceType t,
			@NonNull MacroServer s) {
		servers.put(t, s);
		
		// Adds the events
		for(ServerManagerListener l : 
			listeners.getListeners(ServerManagerListener.class)) {
			s.addEventListener(l);
		}
		
		// Generate the event of adding a new server
		fireServerStart(s);
	}
	
	
	
	/**
	 * Close the given server, if present
	 * @param s Type of server to close
	 */
	public void closeMacroServer(@NonNull ServiceType s) {
		MacroServer server = servers.get(s);
		if(server != null) {
			servers.remove(s);
			server.close();
			
			// Remove associated events
			for(MacroServer.EventListener l : 
				listeners.getListeners(ServerManagerListener.class)) {
				server.removeEventListener(l);
			}
		}
	}
	
	
	
	/**
	 * Closes all active servers
	 * @see #closeMacroServer(ServiceType)
	 */
	public void closeAllServers() {
		// Doing the cloning because it will be modified in the foreach
		Set<ServiceType> se = new HashSet<>(servers.keySet());
		for(ServiceType t : se) {
			closeMacroServer(t);
		}
	}
	
	
	/**
	 * Verify the presence of a server
	 * @param s Type of server
	 * @return True if the server that offer the given service is present, false
	 * otherwise
	 */
	public boolean isMacroServerPresent(@NonNull ServiceType s) {
		return servers.containsKey(s);
	}
	
	
	
	/**
	 * Generates the event of a server creation
	 * @param server Created server
	 */
	private void fireServerStart(@NonNull MacroServer server) {
		assert server != null;
		
		for(ServerManagerListener l : 
			listeners.getListeners(ServerManagerListener.class)) {
			l.onStart(server);
		}
	}
	
	
	/**
	 * @param s Requested server type
	 * @return Server present for the given service; null if not present
	 */
	public MacroServer getMacroServer(@NonNull ServiceType s) {
		return servers.get(s);
	}
	
	
	
	/**
	 * Gets the service type given the service
	 * @param server Server for which to discover the service
	 * @return Service offered by the given server
	 * @throws IllegalArgumentException If {@code server} is not
	 * {@link MacroNetServer} or {@link MacroBluetoothServer}
	 */
	public static @NonNull ServiceType serverService(@NonNull MacroServer server) {
		Objects.requireNonNull(server);
		
		// TODO: should not use instanceof
		if (server instanceof MacroNetServer) {
			return ServiceType.TCP_IP;
		} else if(server instanceof MacroBluetoothServer) {
			return ServiceType.Bluetooth;
		} else {
			throw new IllegalArgumentException("Server service not found");
		}
	}
	
	
	
	/**
	 * Listener for {@link MacroServer} events
	 */
	public interface ServerManagerListener extends EventListener, MacroServer.EventListener {
		
		/**
		 * Called at the server creation
		 * @param server Created server
		 */
		public void onStart(@NonNull MacroServer server);
	}
}
