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
 * Componente della GUI che permette di mostrare i log del server
 */
public class ServerLogComponent extends JPanel {
	
	/** 
	 * Manager dei log relativi ai {@link MacroServer};
	 * null se this è ustato per mostrare 
	 * */
	private MacroServerLogManager logMnager;
	
	
	private final DefaultListModel<LogEvent> items = new DefaultListModel<>();
	private final JList<LogEvent> lstItems = new JList<>(items);
	
	/**
	 * @param logMnager Manager dal quale ottenere i log
	 * @throws NullPointerException Se {@code logManager} è null
	 */
	public ServerLogComponent(MacroServerLogManager logMnager) {
		Objects.requireNonNull(logMnager);
		
		init();
		
		this.logMnager = logMnager;
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
	 * @param logs Cursore per i risultati della query
	 * @throws NullPointerException Se {@code logs} è null
	 */
	public ServerLogComponent(LogRecordCursor logs) {
		Objects.requireNonNull(logs);
		
		init();
		
		Collection<LogRecord> rec = logs.fetchRecords(Integer.MAX_VALUE);
		for(LogRecord l : rec) {
			LogEvent e = new LogEvent(l.date, l.type, l.message);
			items.addElement(e);
		}
	}
	
	
	/**
	 * Inizializzazione comune ai costruttori
	 */
	private void init() {
		setLayout(new BorderLayout(0, 0));
		lstItems.setCellRenderer(new LogItemComponent());
		add(lstItems);
	}
	
	
	/**
	 * Racchiude i dati di un evento di log
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
