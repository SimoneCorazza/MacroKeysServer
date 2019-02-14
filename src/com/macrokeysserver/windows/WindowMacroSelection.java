//package com.macrokeysserver.windows;
//
//import javax.swing.JDialog;
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//
//import java.awt.AWTException;
//import java.awt.Window;
//import java.lang.reflect.GenericSignatureFormatError;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import javax.swing.JButton;
//import javax.swing.JTextField;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.filechooser.FileView;
//import javax.swing.plaf.FileChooserUI;
//
//import com.macrokeysserver.netcode.Server;
//import com.macrokeysserver.util.IOUtil;
//
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//
//import java.awt.event.ActionListener;
//import java.awt.event.WindowEvent;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.awt.event.ActionEvent;
//
//
///** Finestra per la selezione della macro */
//public class WindowMacroSelection extends JDialog {
//	
//	/** Contiene la path alla MacroSetup */
//	private JTextField txtMacroSetupPath;
//	/** Contiene la path alla macro table */
//	private JTextField txtMacroTablePath;
//	
//	/** Bytes rappresentanti la {@link MacroSetup} */
// 	private byte[] macroSetupData;
//
//	/**
//	 * @param parent - Fienstra generatrice di this
//	 * @param pathMacroSetup - Valore iniziale del percorso del file della macro setup; null sinonimo di stringa vuota
//	 * @param pathMacroTable - Valore iniziale del percorso del file della macro table; null sinonimo di stringa vuota
//	 */
//	public WindowMacroSelection(Window parent, String pathMacroSetup, String pathMacroTable) {
//		super(parent, ModalityType.DOCUMENT_MODAL);
//		
//		setBounds(100, 100, 450, 300);
//		setTitle("Macro selection");
//		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		setResizable(false);
//		getContentPane().setLayout(null);
//		
//		JButton btnCancel = new JButton("Cancel");
//		btnCancel.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				btnCancel_Click(e);
//			}
//		});
//		btnCancel.setBounds(345, 237, 89, 23);
//		getContentPane().add(btnCancel);
//		
//		JButton btnOk = new JButton("OK");
//		btnOk.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				btnOk_Click(e);
//			}
//		});
//		btnOk.setBounds(246, 237, 89, 23);
//		getContentPane().add(btnOk);
//		
//		txtMacroSetupPath = new JTextField();
//		txtMacroSetupPath.setBounds(10, 29, 379, 20);
//		getContentPane().add(txtMacroSetupPath);
//		txtMacroSetupPath.setText(pathMacroSetup);
//		txtMacroSetupPath.setColumns(10);
//		
//		JButton btnMacroSetupSelection = new JButton("...");
//		btnMacroSetupSelection.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				btnMacroSetupSelection_Click(e);
//			}
//		});
//		btnMacroSetupSelection.setBounds(399, 28, 35, 23);
//		getContentPane().add(btnMacroSetupSelection);
//		
//		JLabel lblNewLabel = new JLabel("Macro XML file:");
//		lblNewLabel.setBounds(10, 11, 89, 14);
//		getContentPane().add(lblNewLabel);
//		
//		JLabel lblTableFileCsv = new JLabel("Table file CSV:");
//		lblTableFileCsv.setBounds(10, 60, 89, 14);
//		getContentPane().add(lblTableFileCsv);
//		
//		txtMacroTablePath = new JTextField();
//		txtMacroTablePath.setColumns(10);
//		txtMacroTablePath.setText(pathMacroTable);
//		txtMacroTablePath.setBounds(10, 78, 379, 20);
//		getContentPane().add(txtMacroTablePath);
//		
//		JButton btnMacroTableSelection = new JButton("...");
//		btnMacroTableSelection.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				btnMacroTableSelection_Click(e);
//			}
//		});
//		btnMacroTableSelection.setBounds(399, 77, 35, 23);
//		getContentPane().add(btnMacroTableSelection);
//		
//		getRootPane().setDefaultButton(btnOk);
//		
//		setLocationRelativeTo(parent);
//		setVisible(true);
//	}
//	
//	
//	
//	private void btnOk_Click(ActionEvent e) {
//		Path path = Paths.get(txtMacroSetupPath.getText());
//		try {
//			macroSetupData = Files.readAllBytes(path);
//			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
//		} catch (IOException ex) {
//			JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), 
//					"Error while reading files", JOptionPane.ERROR_MESSAGE);
//		}
//	}
//	
//	private void btnCancel_Click(ActionEvent e) {
//		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
//	}
//	
//	private void btnMacroSetupSelection_Click(ActionEvent e) {
//		setTextBoxPath(txtMacroSetupPath, false,
//				new FileNameExtensionFilter("Text file (.txt) | XML file (.xml)", 
//						"txt",
//						"xml"
//						));
//	}
//	
//	private void btnMacroTableSelection_Click(ActionEvent e) {
//		setTextBoxPath(txtMacroTablePath, false,
//				new FileNameExtensionFilter("Text file (.txt) | CSV file (.csv)", 
//						"txt",
//						"csv"
//						));
//	}
//	
//	/**
//	 * Imposta il testo della text box 
//	 * @param txt - Text box nel quale inserire la path
//	 * @param filterAll - True: mostra il filtro che mostra tutti i file
//	 * @param filter - Filtro per gli attributi
//	 */
//	private void setTextBoxPath(JTextField txt, boolean filterAll, FileFilter filter) {
//		assert txt != null;
//		
//		JFileChooser f = new JFileChooser();
//		f.setFileFilter(filter);
//		f.setAcceptAllFileFilterUsed(filterAll);
//		f.setCurrentDirectory(new File(System.getProperty("user.dir")));
//		if(f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//			String path = f.getSelectedFile().getAbsolutePath();
//			txt.setText(path);
//		}
//	}
//
//
//
//	/**
//	 * @return La macro setup, in forma di dati selezionata dall'utente; null se 
//	 * l'operazione è annullata
//	 */
//	public byte[] getMacroSetup() {
//		return macroSetupData;
//	}
//	
//	/**
//	 * @return Path scritta dall'utente per il file della macro setup prima
//	 * della chiusura della dialog
//	 */
//	public String getMacroSetupPath() {
//		return txtMacroSetupPath.getText();
//	}
//	
//	/**
//	 * @return Path scritta dall'utente per il file della macro table prima
//	 * della chiusura della dialog
//	 */
//	public String getMacroTablePath() {
//		return txtMacroTablePath.getText();
//	}
//}
