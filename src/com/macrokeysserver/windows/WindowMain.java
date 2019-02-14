package com.macrokeysserver.windows;


import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.macrokeys.MSLoadException;
import com.macrokeys.MacroKey;
import com.macrokeys.MacroSetup;
import com.macrokeys.comunication.MacroServer;
import com.macrokeysserver.MacroServerManager;
import com.macrokeysserver.MacroServerManager.ServerManagerListener;
import com.macrokeysserver.ServiceType;
import com.macrokeysserver.gui.FileChoosers;
import com.macrokeysserver.logging.LogDatabase;
import com.macrokeysserver.logging.MacroServerLogManager;
import com.macrokeysserver.logging.ServerLogComponent;
import com.macrokeysserver.option.OptionListener;
import com.macrokeysserver.option.Options;

import java.awt.AWTException;
import java.awt.BorderLayout;
import javax.swing.JCheckBoxMenuItem;


import java.io.IOException;
import java.sql.SQLException;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;

public class WindowMain extends JFrame {

	/** Manager dei server */
	private final MacroServerManager manager;
	/** Manager per il log delle {@link MacroServer} */
	private final MacroServerLogManager logManager;
	/** Gestisce i log memorizzati nel database (memorizzazione e query) */
	private LogDatabase dbLogger;
	
	private JMenuItem mniStartAllServers;
	private JMenuItem mniStopAllServers;
	private JMenuItem mniSetMacroSetup;
	private JCheckBoxMenuItem mniSuspendMacExec;
	private ServerLogComponent logger;
	
	/** Chache del percorso al file che ha generato l'attuale macro setup;
		null se non ancora impostata
	 */
	private String pathMacroSetup;
	private JMenuItem mniStartNetServer;
	private JMenuItem mniStartBluetoothServer;
	private JMenuItem mniStopNetServer;
	private JMenuItem mniStopBluetoothServer;
	private JMenu mniRecentMacroSetups;
	
	public WindowMain() {
		Options.load();
		
		this.manager = new MacroServerManager();
		this.logManager = new MacroServerLogManager(manager);
		
		try {
			this.dbLogger = new LogDatabase(logManager);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, 
					"Error while loading the log database:\n" + e.getMessage());
			System.exit(1);
		}
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// Nienete
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// Nienete
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// Nienete
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// Nienete
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				onClose();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// Nienete
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// Nienete
			}
		});
		
		// Listener per il MenuItem
		manager.addListener(new MenuGUIHandler());
		
		
		initializeGUI();
	}


	private void initializeGUI() {
		setTitle("Macro Keys");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnConnection = new JMenu("     Server     ");
		menuBar.add(mnConnection);
		
		mniStartAllServers = new JMenuItem("Start all servers");
		mniStartAllServers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean notCancel = createServer(ServiceType.TCP_IP);
				if(notCancel) {
					createServer(ServiceType.Bluetooth);					
				}
			}
		});
		mniStartAllServers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
		mnConnection.add(mniStartAllServers);
		
		mniStopAllServers = new JMenuItem("Stop all servers");
		mniStopAllServers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				manager.closeAllServers();
			}
		});
		mniStopAllServers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		mniStopAllServers.setEnabled(false);
		mnConnection.add(mniStopAllServers);

		
		mniSuspendMacExec = new JCheckBoxMenuItem("Suspend macro execution");
		mniSuspendMacExec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mniSuspendMacExec_Selected();
			}
		});
		
		JSeparator separator = new JSeparator();
		mnConnection.add(separator);
		
		mniStartNetServer = new JMenuItem("Start net server");
		mniStartNetServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createServer(ServiceType.TCP_IP);
			}
		});
		mnConnection.add(mniStartNetServer);
		
		mniStopNetServer = new JMenuItem("Stop net server");
		mniStopNetServer.setEnabled(false);
		mniStopNetServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.closeMacroServer(ServiceType.TCP_IP);
			}
		});
		mnConnection.add(mniStopNetServer);
		
		JSeparator separator_1 = new JSeparator();
		mnConnection.add(separator_1);
		
		mniStartBluetoothServer = new JMenuItem("Start bluetooth server");
		mniStartBluetoothServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createServer(ServiceType.Bluetooth);
			}
		});
		mnConnection.add(mniStartBluetoothServer);
		
		mniStopBluetoothServer = new JMenuItem("Stop bluetooth server");
		mniStopBluetoothServer.setEnabled(false);
		mniStopBluetoothServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.closeMacroServer(ServiceType.Bluetooth);
			}
		});
		mnConnection.add(mniStopBluetoothServer);
		
		JSeparator separator_2 = new JSeparator();
		mnConnection.add(separator_2);
		mniSuspendMacExec.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mniSuspendMacExec.setEnabled(false);
		mnConnection.add(mniSuspendMacExec);
		
		JMenu mnNewMenu = new JMenu("Macro");
		menuBar.add(mnNewMenu);
		
		mniSetMacroSetup = new JMenuItem("Change macro setup...");
		mniSetMacroSetup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mniSetMacroSetup_Selected();
			}
		});
		mniSetMacroSetup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
		mnNewMenu.add(mniSetMacroSetup);
		
		JMenuItem mniShowLogs = new JMenuItem("Show logs");
		mniShowLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
		mniShowLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WindowLogs w = new WindowLogs(WindowMain.this, dbLogger);
				w.setVisible(true);
			}
		});
		
		mniRecentMacroSetups = new JMenu("Recent macro setups");
		mniRecentMacroSetups.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				mniRecentMacroSetups.removeAll();
				for(String path : Options.macroSetupsFiles.getObjects()) {
					JMenuItem m = new JMenuItem(path);
					mniRecentMacroSetups.add(m);					
				}
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				// Niente
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				// Niente
			}
		});
		mnNewMenu.add(mniRecentMacroSetups);
		
		JSeparator separator_3 = new JSeparator();
		mnNewMenu.add(separator_3);
		mnNewMenu.add(mniShowLogs);
		
		
		logger = new ServerLogComponent(logManager);
		JScrollPane scrollPane = new JScrollPane(logger);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        getContentPane().add(scrollPane);
		
		
		setVisible(true);
	}


	
	

	
	private void mniSetMacroSetup_Selected() {
		if(manager.isAtLeastOneServerPresent()) {
			String path = FileChoosers.macroSetup(this);
			if(path != null) {				
				MacroSetup setup;
				try {
					setup = MacroSetup.load(path);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), 
							"Error while reading file", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (MSLoadException e) {
					JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), 
							"Error while loading the file", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				
				manager.changeMacroSetup(setup);
				pathMacroSetup = path;
				
				// Aggiungo la path alla relativa cronologia
				Options.macroSetupsFiles.add(path);
			}
			
		}
	}
	
	/** Click sul relativo men� item */
	private void mniSuspendMacExec_Selected() {
		boolean sel = mniSuspendMacExec.isSelected();
		for(MacroServer s : manager.servers()) {
			s.setSuspend(sel);
		}
	}
	
	
	/**
	 * Operazioni eseguite prima della chiusura dell'applicazione
	 */
	private void onClose() {
		manager.closeAllServers();
		Options.save();
	}
	
	
	/**
	 * Crea il server indicato, se non già creato
	 * @param t Tipologia di comunicazione del server
	 * @return True se l'operazione non è stata cancellata dall'utente, False altrimenti
	 * L'operazione viene cancellata se clicca Annulla nella selezione della MacroSetup
	 */
	private boolean createServer(ServiceType t) {
		assert t != null;
		// Caso server già presente non faccio niente
		if(manager.isMacroServerPresent(t)) {
			return true;
		}
		
		
		// Se nessuna MacroSetup è stata selezionata
		if(manager.getMacroSetup() == null) {
			String path = FileChoosers.macroSetup(this);
			if(path == null) {
				return false;
			}
			
			MacroSetup setup = null;
			try {
				setup = MacroSetup.load(path);
			} catch (IOException | MSLoadException e) {
				JOptionPane.showMessageDialog(null,
						"Cannot load the file.",
						"Error while loading",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			assert setup != null;
			// Inizializzo il valore della setup
			manager.changeMacroSetup(setup);
			
			// Aggiungo la path alla relativa cronologia
			Options.macroSetupsFiles.add(path);
		}
		
		try {
			switch(t) {
			case TCP_IP:
				try {
					manager.macroNetServer();
				} catch(IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
						"Error while creating the server: " + e.getMessage(),
						"Error creating server",
						JOptionPane.ERROR_MESSAGE);
				}
				break;
			
			case Bluetooth:
				try {
					manager.macroBluetoothServer();
				} catch(IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
						"Error while creating blotooth server. Make sure that the bluetooth adapter is present and activated",
						"Error creating server",
						JOptionPane.ERROR_MESSAGE);
				}
				break;
				
				default: assert false;
			}
		} catch(AWTException e) {
			JOptionPane.showMessageDialog(null,
					"Error while creating the server: " + e.getMessage(),
					"Error creating server",
					JOptionPane.ERROR_MESSAGE);
		}
		
		return true;
	}
	
	
	/**
	 * Listener per gli eventi dei servers di {@link MacroServerManager}
	 * per aggiornare l'UI del Menu
	 */
	private class MenuGUIHandler implements ServerManagerListener {
		
		@Override
		public void onSuspendChanged(MacroServer server, boolean newState) {
			mniSuspendMacExec.setSelected(newState);
		}
		
		@Override
		public void onMacroSetupChanged(MacroServer server,
				MacroSetup actual) {
			// Niente
		}
		
		@Override
		public void onKeyReceved(MacroServer server, String sender,
				MacroKey mk, boolean action) {
			// Nienete
		}
		
		@Override
		public void onDisconnectListener(MacroServer server, String sender) {
			// Niente
		}
		
		@Override
		public void onConnectListener(MacroServer server, String sender) {
			// Niente
		}
		
		@Override
		public void onClose(MacroServer server) {
			if(!manager.isAtLeastOneServerPresent()) {
				mniStartAllServers.setEnabled(true);
				mniStopAllServers.setEnabled(false);
				mniSetMacroSetup.setEnabled(false);
			}
			
			updateMenuInteraction(server, false);
		}
		
		@Override
		public void onStart(MacroServer server) {
			mniStartAllServers.setEnabled(false);
			mniStopAllServers.setEnabled(true);
			mniSetMacroSetup.setEnabled(true);
			
			updateMenuInteraction(server, true);
		}
		
		
		/**
		 * Abilita/Disabilita i relativi menù in base all'evento occorso
		 * @param s Server soggetto dell'evento; non null
		 * @param e Evento intercorso. True server partito; False server fermato
		 */
		private void updateMenuInteraction(MacroServer s, boolean e) {
			switch (MacroServerManager.serverService(s)) {
			case TCP_IP:
				mniStartNetServer.setEnabled(!e);
				mniStopNetServer.setEnabled(e);
				break;
				
			case Bluetooth:
				mniStartBluetoothServer.setEnabled(!e);
				mniStopBluetoothServer.setEnabled(e);
				break;
				
			default:
				assert false;
			
			}
			
			// Menù sospensione abilitato sse c'è almeno un server
			boolean stLeastOneServer = manager.isAtLeastOneServerPresent();
			mniSuspendMacExec.setEnabled(stLeastOneServer);
		}
	}
}
