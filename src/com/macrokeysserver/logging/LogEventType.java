package com.macrokeysserver.logging;

/**
 * Tipologia di evento di log
 */
public enum LogEventType {
	ClientConnected,
	ClientDisconnected,
	ClientKeyPress,
	ClientKeyRelease,
	ServerCreated,
	ServerClosed,
	ServerSuspedStateChange,
	ServerMacroSetupChange
}
