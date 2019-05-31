package com.randude14.lotteryplus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/*
 * Extends the Logger class and logs the winners using a date and time stamp
 */
public class WinnersLogger extends java.util.logging.Logger {
	private static final LotteryPlus plugin = LotteryPlus.getInstance();
	private static final String winnersLogString = plugin.getDataFolder() + "/winners.log";
	private static final File winnersLogFile = new File(winnersLogString);
	
	public WinnersLogger() {
		super("WinnersLogger", null);
		setUseParentHandlers(false);
		
		try {
			if(!winnersLogFile.exists())
				winnersLogFile.createNewFile();
			
			FileHandler handler = new FileHandler(winnersLogString);
			handler.setFormatter(new WinnerFormatter());
			handler.setLevel(Level.INFO);
			addHandler(handler); // add file handler
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * Formats the winners with time stamping and the rewards that were given
	 */
	class WinnerFormatter extends Formatter {
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("[yyyy-MMM-ddd] [hh:mm:ss]");
		
		public String format(LogRecord record) {
			StringBuffer sb = new StringBuffer();
			sb.append(dateFormatter.format(new Date()));
			sb.append(" - ");
			sb.append(record.getMessage());
			sb.append("\n");
			return sb.toString();
		}
	}
	
	/*
	 * Called on closing of this logger. Closes all handlers
	 */
	public void close() {
		for(Handler handler : this.getHandlers()) {
			try {
				handler.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
