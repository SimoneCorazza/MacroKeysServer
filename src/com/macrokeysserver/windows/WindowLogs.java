package com.macrokeysserver.windows;

import java.awt.Window;
import java.util.Objects;

import javax.swing.JDialog;

import com.macrokeysserver.logging.LogDatabase;
import com.macrokeysserver.logging.LogDatabase.LogRecordCursor;
import com.macrokeysserver.logging.ServerLogComponent;

/**
 * Finestra per mostrare la storia dei log
 */
public class WindowLogs extends JDialog {
	
	private final ServerLogComponent logs;
	
	public WindowLogs(Window parent, LogDatabase dbLog) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		Objects.requireNonNull(dbLog);
		
		setTitle("Log history");
		setSize(800, 600);
		
		LogRecordCursor cursor = dbLog.historyLog();
		logs = new ServerLogComponent(cursor);
		getContentPane().add(logs);
	}

}
