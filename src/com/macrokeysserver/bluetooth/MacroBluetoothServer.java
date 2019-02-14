package com.macrokeysserver.bluetooth;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;

import com.macrokeys.MacroSetup;
import com.macrokeys.comunication.MacroServer;
import com.macrokeys.comunication.MessageProtocol;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * Implementazione di 
 */
public class MacroBluetoothServer extends MacroServer {
	
	/**
	 * UUID per il servizio di bluetooth
	 */
	private static final String UUID = "a69cea44c6dd11e7abc4cec278b6b50a";
	

	/** Indica se il server accetta connessioni */
	private boolean seekConnection = true;
	
	/**
	 * Per la gestione delle connessioni di nuovi client bluetooth
	 */
	private StreamConnectionNotifier connectionNotifier;
	
	
	/**
	 * @param setup Setup inizialmente utilizzata
	 * @throws AWTException In caso di errore nell'inizializzazione di {@link Robot}
	 */
	public MacroBluetoothServer(MacroSetup setup) throws AWTException {
		super(setup);
	}
	
	
	
	@Override
	protected void innerStart() throws IOException {
		connectionNotifier = (StreamConnectionNotifier) Connector.open(
				"btspp://localhost:" + UUID + ";authenticate=true;encrypt=true");
	}
	
	
	
	@Override
	protected void introduceServerToClient() {
		// Non usato
	}
	
	
	
	@Override
	protected MessageProtocol waitNewClientConnection() throws IOException {
		StreamConnection connection = connectionNotifier.acceptAndOpen();
		return new BluetoothMessageProtocol(connection);
	}

	

	@Override
	public boolean isSeekConnection() {
		return seekConnection;
	}

	
	
	@Override
	public void setSeekConnection(boolean b) {
		seekConnection = b;
	}

	
	
	@Override
	protected void innerClose() {
		try {
			connectionNotifier.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
