package com.macrokeysserver.components;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/** Componente per ottenere un numero */
public class NumberComponent extends JFormattedTextField {
	
	public NumberComponent() {
		NumberFormat f = NumberFormat.getInstance();
		NumberFormatter nf = new NumberFormatter(f);
		DefaultFormatterFactory fac = new DefaultFormatterFactory(nf);
		super.setFormatterFactory(fac);
	}
	
	@Override
	public void setFormatterFactory(AbstractFormatterFactory arg0) {
		int c = 0;
	}
	
}
