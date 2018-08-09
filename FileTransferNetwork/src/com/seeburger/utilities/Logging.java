package com.seeburger.utilities;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging
{
	public static Logger logger = Logger.getLogger("FileLog");
	private static FileHandler fHandler;

	public static void initializeLogger(String module)
	{
		try
		{
			fHandler = new FileHandler(module + ".log");
			logger.addHandler(fHandler);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fHandler.setFormatter(simpleFormatter);
		} catch (SecurityException | IOException e2)
		{
			logger.log(Level.WARNING, e2.getMessage(), e2);
			e2.printStackTrace();
		}
	}
}
