package com.macrokeysserver.logging;

/**
 * Log type
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
