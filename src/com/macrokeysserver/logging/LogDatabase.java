package com.macrokeysserver.logging;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.comunication.MacroServer;
import com.macrokeysserver.logging.MacroServerLogManager.LogEventListener;

/**
 * Memorizza i log all'interno del database
 */
public class LogDatabase {
	
	private static final String CREATE_TABLE_IF_EXISTS = 
		"CREATE TABLE IF NOT EXISTS LOGS (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," + // ID della tabella
		// Campi sempre presenti:
		"date INTEGER NOT NULL," + // Data del log
		"type INTEGER NOT NULL," + // Tipologia di evento; ordinale dell'enum LogEventType
		"server VARCHAR(50) NOT NULL," + // Tipologia di server che genera l'evento
		"message TEXT NOT NULL," + // Messaggio del log
		// Campi presenti in base alla tipologia di log:
		"client VARCHAR(100)" + // Nome del client; NULL se il client non c'entra con l'evento loggato
		")";
	
	private static final String INSERT_LOG = 
			"INSERT INTO logs(date, type, server, message, client) " +
			"VALUES(?, ?, ?, ?, ?)";
	
	
	private final MacroServerLogManager logManager;
	private final Connection connection;
	
	private final Statement statement;
	private final PreparedStatement insertStm;
	
	private final ThreadPoolExecutor thpool;
	private final LinkedBlockingQueue<Runnable> runnablesQueque;
	
	/**
	 * @param logManager Manager dei log
	 * @throws SQLException Se c'è un errore nella query
	 */
	public LogDatabase(MacroServerLogManager logManager)
			throws SQLException {
		Objects.requireNonNull(logManager);
		this.logManager = logManager;
		
		this.connection = DriverManager.getConnection("jdbc:sqlite:log.sqlite");
		this.statement = connection.createStatement();
		statement.executeUpdate(CREATE_TABLE_IF_EXISTS);
		
		this.insertStm = connection.prepareStatement(INSERT_LOG);
		
		runnablesQueque = new LinkedBlockingQueue<>();
		thpool = new ThreadPoolExecutor(1, 1, 5000,
				TimeUnit.MILLISECONDS, runnablesQueque);
		
		logManager.addLogEventListener(new Listener());
	}
	
	
	
	/**
	 * Ottiene i log memorizzati
	 * @return Cursore per scorrere i vari risultati
	 */
	public LogRecordCursor historyLog() {
		try {
			ResultSet res = statement.executeQuery("SELECT * FROM logs ORDER BY date DESC");
			return new LogRecordCursor(res);
		} catch (SQLException e) {
			assert false;
			throw new AssertionError();
		}
	}
	
	
	
	
	/**
	 * Listener per gli eventi dei {@link MacroServer}
	 */
	private class Listener implements LogEventListener {

		@Override
		public void log(Date time, LogEventType type, String serverType,
				String client, String message) {
			thpool.execute(new Runnable() {
				
				@Override
				public void run() {
					long date = time.getTime();
					int typeInt = type.ordinal();
					
					try {
						insertStm.setLong(1, date);
						insertStm.setInt(2, typeInt);
						insertStm.setString(3, serverType);
						insertStm.setString(4, message);
						insertStm.setString(5, client);
						
						insertStm.executeUpdate();
					} catch(SQLException sqle) {
						try {
							insertStm.clearParameters();
						} catch(SQLException ignored) {
							// Ignoro
						}	
					}
				}
			});
		}
	}
	
	
	/**
	 * Classe che rappresetna un record immutabile della tabella di log
	 */
	public class LogRecord {
		public final long id;
		public final Date date;
		public final LogEventType type;
		public final String serverType;
		public final String client;
		public final String message;
		
		LogRecord(long id, long date, int eventType, String serverType,
				String client, String message) {
			this.id = id;
			this.date = new Date(date);
			this.type = LogEventType.values()[eventType];
			this.serverType = serverType;
			this.client = client;
			this.message = message;
		}
	}
	
	
	
	/**
	 * Cursore per scorrere {@link LogRecord} ottenuti da una query
	 */
	public class LogRecordCursor {
		
		private final ResultSet set;
		
		LogRecordCursor(ResultSet set) {
			Objects.requireNonNull(set);
			this.set = set;
		}
		
		
		
		/**
		 * Chiamata sincrona per ottenere i record dalla query eseguita
		 * @param num Numero massimo di record da ottenere
		 * @return Collezzione contenente i record; mai null
		 * @throws IllegalArgumentException Se {@code num} è <= 0
		 */
		public Collection<LogRecord> fetchRecords(int num) {
			if(num <= 0) {
				throw new IllegalArgumentException("Number must be > 0");
			}
			
			List<LogRecord> l = new LinkedList<>();
			try {
				int i = 0;
				while(i < num && set.next()) {
					long id = set.getLong("id");
					long date = set.getLong("date");
					int eventType = set.getInt("type");
					String serverType = set.getString("server");
					String message = set.getString("message");
					String client = set.getString("client");
					
					l.add(new LogRecord(id, date, eventType, serverType,
							client, message));
					
					i++;
				}
			} catch(SQLException e) {
				assert false;
			}
			
			
			return l;
		}
	}
}
