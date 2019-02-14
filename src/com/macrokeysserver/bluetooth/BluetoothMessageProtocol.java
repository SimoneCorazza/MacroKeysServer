package com.macrokeysserver.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.StreamConnection;

import com.macrokeys.comunication.MessageProtocol;


/**
 * Implementazione del {@link MessageProtocol} per la comunicazione Bluetooth.
 * <p>
 * Il timeout non è stato implementato siccome la disconnessione viene
 * rilevata automaticamente dallo stato sottostante
 * </p>
 */
public class BluetoothMessageProtocol implements MessageProtocol {

	private final DataInputStream in;
	private final DataOutputStream out;
	private final StreamConnection connection;
	
	private boolean connected;
	
	
	public BluetoothMessageProtocol(StreamConnection conn) throws IOException {
		this.in = conn.openDataInputStream();
		this.out = conn.openDataOutputStream();
		this.connection = conn;
		
		connected = true;
	}
	
	
	
	@Override
	public boolean isConnected() {
		return connected;
	}

	
	
	@Override
	public void setInputKeepAlive(int time) {
		// Implementazione non necessaria
	}

	
	
	@Override
	public int getInputKeepAlive() {
		return 0; // Implementazione non necessaria
	}

	
	
	@Override
	public void setOutputKeepAlive(int time) {
		// Implementazione non necessaria
	}

	
	
	@Override
	public int getOutputKeepAlive() {
		return 0; // Implementazione non necessaria
	}

	
	
	@Override
	public void sendMessage(byte[] payload) throws IOException {
		out.writeByte(1);
		out.writeByte(0);
		out.writeInt(payload.length);
		out.write(payload);
	}

	
	
	@Override
	public byte[] receiveMessage() throws IOException {
		byte v1 = in.readByte();
		byte v2 = in.readByte();
		if(v1 != 1 || v2 != 0) {
			throw new IOException("Version of message not known");
		}
		
		int length = in.readInt();
		byte[] payload = new byte[length];
		in.readFully(payload);
		
		return payload;
	}

	
	
	@Override
	public void close() throws IOException {
		if(isConnected()) {
			connection.close();
			connected = false;
		}
	}

}
