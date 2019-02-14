package com.macrokeysserver.logging;

import java.util.Date;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroSetup;
import com.macrokeys.comunication.MacroServer;
import com.macrokeys.netcode.MacroNetServer;
import com.macrokeysserver.MacroServerManager;
import com.macrokeysserver.MacroServerManager.ServerManagerListener;
import com.macrokeysserver.ServiceType;
import com.macrokeysserver.bluetooth.MacroBluetoothServer;

/**
 * Gestisce il log degli eventi dei server ed ad essi associati.
 * <p>
 * In pratica converte gli eventi generati dal {@link MacroServerManager} e da
 * {@link MacroServer} in eventi più opportuni per un sistema di log.
 * Questo viene eseguito mediante un evento
 * </p>
 */
public class MacroServerLogManager {

	private final EventListenerList listeners = new EventListenerList();
	
	
	public MacroServerLogManager(@NonNull MacroServerManager serverManager) {
		serverManager.addListener(new ServerManagerListener() {
			
			@Override
			public void onSuspendChanged(MacroServer server, boolean newState) {
				String message = newState ? "Server disabled" : "Server enabled";
				fireLogEvent(LogEventType.ServerSuspedStateChange,
						server,
						null,
						message);
			}
			
			@Override
			public void onMacroSetupChanged(MacroServer server,
					MacroSetup actual) {
				String message = "Macro setup updated";
				fireLogEvent(LogEventType.ServerMacroSetupChange,
						server,
						null,
						message);
			}
			
			@Override
			public void onKeyReceved(MacroServer server, String sender,
					MacroKey mk, boolean action) {
				String keys = mk.getKeySeq() + " (" + mk.getType() + ")";
				String message = (action ? "Pressure" : "Release") + " of " + keys;
				LogEventType t = action ? LogEventType.ClientKeyPress :
					LogEventType.ClientKeyRelease;
				fireLogEvent(t,
						server,
						sender,
						message);
			}
			
			@Override
			public void onDisconnectListener(MacroServer server, String sender) {
				String message = "Client disconnected";
				fireLogEvent(LogEventType.ClientDisconnected,
						server,
						sender,
						message);
			}
			
			@Override
			public void onConnectListener(MacroServer server, String sender) {
				String message = "Client connected";
				fireLogEvent(LogEventType.ClientConnected,
						server,
						sender,
						message);
			}
			
			@Override
			public void onClose(MacroServer server) {
				String message = serverType(server) + " server closed";
				fireLogEvent(LogEventType.ServerClosed,
						server,
						null,
						message);
			}
			
			@Override
			public void onStart(MacroServer server) {
				String message = serverType(server) + " server created";
				fireLogEvent(LogEventType.ServerCreated,
						server,
						null,
						message);
			}
		});
	}
	
	
	/**
	 * Aggiunge il listener
	 * @param l Listener da aggiungere
	 */
	public void addLogEventListener(LogEventListener l) {
		listeners.add(LogEventListener.class, l);
	}
	
	
	/**
	 * Rimuove l'istanza del listener
	 * @param l Evento da rimuovere
	 */
	public void removeLogEventListener(LogEventListener l) {
		listeners.remove(LogEventListener.class, l);
	}
	
	
	/**
	 * Genera l'evento di log per l'evento
	 * @param type Tipologia di evento; non null
	 * @param server Server soggetto dell'evento; non null
	 * @param client Identificativo dle client che ha generato l'evento;
	 * null se nessuno
	 * @param message Messaggio di log; non null
	 */
	private void fireLogEvent(LogEventType type, MacroServer server,
			String client, String message) {
		assert type != null && server != null && message != null;
		
		Date time = new Date();
		String serverType = serverType(server);
		for(LogEventListener l :
			listeners.getListeners(LogEventListener.class)) {
			l.log(time, type, serverType, client, message);
		}
	}
	
	
	/**
	 * Ottiene la stringa che determina la tipologia di server
	 * @param s Server la cui tipologia è da definire
	 * @return Tipologia del server
	 */
	private static String serverType(@NonNull MacroServer s) {
		assert s != null;
		
		ServiceType type = MacroServerManager.serverService(s);
		switch(type) {
		case TCP_IP: return "Net";
		case Bluetooth: return "Bluetooth";
		default: assert false; return null;
		
		}
	}
	
	
	/**
	 * Listener per un evento di log
	 */
	public interface LogEventListener extends EventListener {
		
		/**
		 * Evento di login
		 * @param time Istante temporale dell'evento
		 * @param type Tipologia di evento
		 * @param serverType Stringa che identifica il server (es. TCP/IP, Bluetooth, ...)
		 * @param client Identificativo del client; null se il client non c'entra con il log
		 * @param message Messaggio di log
		 */
		void log(Date time, LogEventType type, String serverType, String client,
				String message);
	}
}
