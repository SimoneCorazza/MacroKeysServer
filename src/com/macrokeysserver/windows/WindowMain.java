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

import org.eclipse.jdt.annotation.NonNull;

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

	/** Server manager */
	private final MacroServerManager manager;
	
	/** Log manager for the {@link MacroServer} */
	private final MacroServerLogManager logManager;
	
	/** Manager for the log database (storage and query) */
	private LogDatabase dbLogger;
	
	private JMenuItem mniStartAllServers;
	private JMenuItem mniStopAllServers;
	private JMenuItem mniSetMacroSetup;
	private JCheckBoxMenuItem mniSuspendMacExec;
	private ServerLogComponent logger;
	
	/** 
	 * Path of the actually used {@link MacroSetup} file; null if not set yet
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
				// Nothing
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				onClose();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// Nothing
			}
		});
		
		// Listener for the MenuItem
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
				// Nothing
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				// Nothing
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
				
				// Adding the path to the chronology
				Options.macroSetupsFiles.add(path);
			}
			
		}
	}
	
	/** Callback for the suspend MenuItem click */
	private void mniSuspendMacExec_Selected() {
		boolean sel = mniSuspendMacExec.isSelected();
		for(MacroServer s : manager.servers()) {
			s.setSuspend(sel);
		}
	}
	
	
	/**
	 * Operations done before the closure of the application
	 */
	private void onClose() {
		manager.closeAllServers();
		Options.save();
	}
	
	
	/**
	 * Create the given server, if not yet created
	 * @param t Server type
	 * @return True if the operation was not cancelled by the user, False otherwise
	 */
	private boolean createServer(ServiceType t) {
		assert t != null;
		
		// If server is present do nothing
		if(manager.isMacroServerPresent(t)) {
			return true;
		}
		
		
		// If no MacroSetup was not selected
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
			
			// Init the value of the MacroSetup
			manager.changeMacroSetup(setup);
			
			// Add the path in the chronology
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
	 * Listener for the server events {@link MacroServerManager}
	 * to update the UI of the menus
	 */
	private class MenuGUIHandler implements ServerManagerListener {
		
		@Override
		public void onSuspendChanged(MacroServer server, boolean newState) {
			mniSuspendMacExec.setSelected(newState);
		}
		
		@Override
		public void onMacroSetupChanged(MacroServer server,
				MacroSetup actual) {
			// Nothing
		}
		
		@Override
		public void onKeyReceved(MacroServer server, String sender,
				MacroKey mk, boolean action) {
			// Nothing
		}
		
		@Override
		public void onDisconnectListener(MacroServer server, String sender) {
			// Nothing
		}
		
		@Override
		public void onConnectListener(MacroServer server, String sender) {
			// Nothing
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
		 * Enable/Disable the menus based on the fired event
		 * @param s Server subject of the event
		 * @param e True if server started, False server stopped
		 */
		private void updateMenuInteraction(@NonNull MacroServer s, boolean e) {
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
			
			// Suspension menu enabed iif there is at least one server
			boolean stLeastOneServer = manager.isAtLeastOneServerPresent();
			mniSuspendMacExec.setEnabled(stLeastOneServer);
		}
	}
}
