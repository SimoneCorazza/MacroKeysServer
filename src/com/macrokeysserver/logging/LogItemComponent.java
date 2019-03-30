package com.macrokeysserver.logging;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeysserver.logging.ServerLogComponent.LogEvent;

public class LogItemComponent implements ListCellRenderer<LogEvent> {
	
	private final List<ItemC> controls = new ArrayList<>();

	
	private static final Color SERVER_STOP = Color.RED;
	private static final Color SERVER_RESUME = Color.GREEN;
	private static final Color SERVER_SUSPEND = Color.ORANGE;
	private static final Color SERVER_CREATION = Color.MAGENTA;
	private static final Color SERVER_CLIENT_CONNECT = Color.GRAY;
	private static final Color SERVER_CLIENT_DISCONNECT = Color.YELLOW;
	private static final Color SERVER_MACROSETUP_UPDATE = Color.PINK;
	private static final Color SERVER_MACROID_RECIVED = Color.CYAN;
	
	
	
	public LogItemComponent() {
		
	}
	
	
	
	@Override
	public Component getListCellRendererComponent(
			JList<? extends LogEvent> list,
			LogEvent value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
		
		ItemC c;
		if(index >= controls.size()) {
			c = new ItemC(value);
			controls.add(c);
		} else {
			c = controls.get(index);
		}
		
		
		
		
		c.setSelected(isSelected);

		
		
		return c;
	}
	
	
	/**
	 * Format the given date
	 * @param d Date to format
	 * @return Formatted date
	 */
	private static String formatDate(@NonNull Date d) {
		assert d != null;
		
		return new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss.SSS]").format(d);
	}
	
	
	
	/**
	 * Log item
	 */
	private static class ItemC extends JPanel {
		
		private final LogEvent args;
		private boolean selected = false;
		
		
		public ItemC(LogEvent e) {
			assert e != null;
			
			this.args = e;
			
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEFT);
			setLayout(layout);
			
			
			String date = formatDate(e.time);
			JLabel lblTime = new JLabel(date);
			lblTime.setBorder(new EmptyBorder(0, 5, 0, 30));
			add(lblTime);
			
			JLabel lblMessage = new JLabel();
			lblMessage.setText(e.message);
			add(lblMessage);
			
			
			
			
			
			setBackground(eventColor(e.type));
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			g.setColor(Color.BLACK);
			int h = getHeight() - 1;
			g.drawLine(0, h, getWidth() - 1, h);
		}
		
		
		/**
		 * @param t Log type
		 * @return Associated color at the log type
		 */
		private static Color eventColor(@NonNull LogEventType t) {
			assert t != null;
			
			switch(t) {
			case ClientConnected: return SERVER_CLIENT_CONNECT;
			case ClientDisconnected: return SERVER_CLIENT_DISCONNECT;
			case ClientKeyPress: return SERVER_MACROID_RECIVED;
			case ClientKeyRelease: return SERVER_MACROID_RECIVED;
			case ServerClosed: return SERVER_STOP;
			case ServerCreated: return SERVER_CREATION;
			case ServerSuspedStateChange:  return SERVER_SUSPEND;
			case ServerMacroSetupChange: return SERVER_MACROSETUP_UPDATE;
			
			default:
				assert false;
				return Color.BLACK;
			}
		}
		
		
		
		/**
		 * Selected state of this control
		 * @param sel True for selected, false otherwise
		 */
		public void setSelected(boolean sel) {
			if(sel != selected) {
				selected = sel;
				
				Color c = eventColor(args.type);
				if(selected) {
					setBackground(c.darker());
				} else {
					setBackground(c);
				}
			}
		}
	}

}
