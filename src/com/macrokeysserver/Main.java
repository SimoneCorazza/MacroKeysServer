package com.macrokeysserver;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.macrokeysserver.windows.WindowMain;

public class Main {
	

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			
		}
		
		WindowMain window = new WindowMain();
		/*
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowMain window = new WindowMain();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
	}
}
