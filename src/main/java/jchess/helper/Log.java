package jchess.helper;

import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Log {
	private static final String logPath = ".log";
	private static final Logger LOGGER = Logger.getLogger(Log.class.getName());
	private static FileHandler fh;

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

	public static void log(Object obj) {
		if (obj == null)
			return;
		else
			log(obj.toString());
	}

	public static void log(String str) {
		log(Level.INFO, str);
	}

	public static void log(Level level, Object obj) {
		if (obj == null)
			return;
		else
			LOGGER.log(level, obj.toString());
	}

	public static void log(Level level, String str) {
		LOGGER.log(level, str);
	}
}
