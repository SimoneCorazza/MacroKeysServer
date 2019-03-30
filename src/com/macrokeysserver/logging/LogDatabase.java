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
 * Stor the log inside the databse
 */
public class LogDatabase {
	
	private static final String CREATE_TABLE_IF_EXISTS = 
		"CREATE TABLE IF NOT EXISTS LOGS (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," + // Table ID
		// Fields always present:
		"date INTEGER NOT NULL," + // Date of the log
		"type INTEGER NOT NULL," + // Type of the event; ordinal number of the enum LogEventType
		"server VARCHAR(50) NOT NULL," + // Server type that generated the event
		"message TEXT NOT NULL," + // Log message
		// Field present in a per log type base:
		"client VARCHAR(100)" + // Name of the client; NULL if the client does not matter in this logged event
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
	 * @param logManager Log manager
	 * @throws SQLException If an SQL error occur
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
	 * Gets the stored records
	 * @return Cursor to navigate the results
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
	 * Listener for the events of a {@link MacroServer}
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
							// Ignore
						}	
					}
				}
			});
		}
	}
	
	
	/**
	 * Class for an immutable record of the log table
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
	 * Cursor to navigate a {@link LogRecord} obtained by a query
	 */
	public class LogRecordCursor {
		
		private final ResultSet set;
		
		LogRecordCursor(ResultSet set) {
			Objects.requireNonNull(set);
			this.set = set;
		}
		
		
		
		/**
		 * Sincronous call to get the records of an executed query
		 * @param num Maximum number of record to get
		 * @return Record collections
		 * @throws IllegalArgumentException If {@code num} is <= 0
		 */
		public @NonNull Collection<LogRecord> fetchRecords(int num) {
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
