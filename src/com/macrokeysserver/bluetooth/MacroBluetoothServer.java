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
 * Bluetooth implementation of a {@link MacroServer}
 */
public class MacroBluetoothServer extends MacroServer {
	
	/**
	 * UUID for the bluetooth service
	 */
	private static final String UUID = "a69cea44c6dd11e7abc4cec278b6b50a";
	

	/** Indicates if the server accepts new connections */
	private boolean seekConnection = true;
	
	/**
	 * For the managment of the new bluetooth clients
	 */
	private StreamConnectionNotifier connectionNotifier;
	
	
	/**
	 * @param setup Setup initially used
	 * @throws AWTException In case of {@link Robot} init error
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
		// Not used
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
