package com.seeburger.client;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Client used to send commands and receive information from the server of the
 * file transferring app.
 *
 * @author ts.georgiev
 *
 */
public class Main
{
	public static Logger logger = Logger.getLogger("FileLog");
	private static FileHandler fHandler;
	private static Socket socket;


	public static void main(String[] args)
	{

		try
		{
			fHandler = new FileHandler("ClientLog.log");
			logger.addHandler(fHandler);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fHandler.setFormatter(simpleFormatter);
		} catch (SecurityException | IOException e2)
		{
			logger.log(Level.WARNING, e2.getMessage(), e2);
			e2.printStackTrace();
		}


		try
		{
			socket = new Socket("192.168.0.107", 21000);
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

			ListenerThread listenerRunnable = new ListenerThread(dataInputStream);
			Thread listenerThread = new Thread(listenerRunnable);
			CommandsThread commandsRunnable = new CommandsThread(dataOutputStream, socket);
			Thread commandsThread = new Thread(commandsRunnable);
			listenerThread.start();
			commandsThread.start();
		} catch (IOException e)
		{
			logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static Logger getLogger()
	{
		return logger;
	}
}