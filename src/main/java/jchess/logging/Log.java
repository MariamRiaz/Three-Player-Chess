package jchess.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class to log the information regarding the warnings, exceptions and errors that appear during runtime
 */
public class Log {
	private static final String logPath = ".log";
	private static final Logger LOGGER = Logger.getLogger(Log.class.getName());
	private static FileHandler fh;

	/**
	 * method to intialize the file handler for the log file
	 */
	public static void init() {
		LOGGER.setLevel(Level.INFO);

		try {
			fh = new FileHandler(logPath);
			LOGGER.addHandler(fh);

			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs the information about the Object passed
	 */
	public static void log(Object obj) {
		if (obj == null)
			return;
		else
			log(obj.toString());
	}

	/**
	 * Logs the information passed as String
	 */
	public static void log(String str) {
		log(Level.INFO, str);
	}

	/**
	 * Logs the information about the severity of the event and the Object involved
	 */
	public static void log(Level level, Object obj) {
		if (obj == null)
			return;
		else
			LOGGER.log(level, obj.toString());
	}

	/**
	 * Logs the information about the severity of the event and the data passed as String
	 */
	public static void log(Level level, String str) {
		LOGGER.log(level, str);
	}
}
