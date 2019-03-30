package com.macrokeysserver.logging;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.comunication.MacroServer;
import com.macrokeysserver.logging.LogDatabase.LogRecord;
import com.macrokeysserver.logging.LogDatabase.LogRecordCursor;
import com.macrokeysserver.logging.MacroServerLogManager.LogEventListener;

/**
 * GUI component to show the log of a server actions
 */
public class ServerLogComponent extends JPanel {
	
	
	private final DefaultListModel<LogEvent> items = new DefaultListModel<>();
	private final JList<LogEvent> lstItems = new JList<>(items);
	
	/**
	 * @param logMnager Manager of the logs
	 */
	public ServerLogComponent(@NonNull MacroServerLogManager logMnager) {
		Objects.requireNonNull(logMnager);
		
		init();
		
		logMnager.addLogEventListener(new LogEventListener() {
			
			@Override
			public void log(Date time, LogEventType type, String serverType,
					String client, String message) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						LogEvent e = new LogEvent(time, type, message);
						items.addElement(e);
					}
				});
			}
		});
	}
	
	
	/**
	 * @param logs Cursor for the result of a query
	 */
	public ServerLogComponent(@NonNull LogRecordCursor logs) {
		Objects.requireNonNull(logs);
		
		init();
		
		Collection<LogRecord> rec = logs.fetchRecords(Integer.MAX_VALUE);
		for(LogRecord l : rec) {
			LogEvent e = new LogEvent(l.date, l.type, l.message);
			items.addElement(e);
		}
	}
	
	
	/**
	 * Common initializzation for the constructors
	 */
	private void init() {
		setLayout(new BorderLayout(0, 0));
		lstItems.setCellRenderer(new LogItemComponent());
		add(lstItems);
	}
	
	
	/**
	 * Log data
	 */
	class LogEvent {
		final Date time;
		final LogEventType type;
		final String message;
		
		LogEvent(Date time, LogEventType type, String message) {
			this.time = time;
			this.type = type;
			this.message = message;
		}
	}
}
