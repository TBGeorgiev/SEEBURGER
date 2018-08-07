package com.seeburger.client;

import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.seeburger.utilities.Authentication;

/**
 * Client used to send commands and receive information from the server of the
 * file transferring application.
 *
 * @author ts.georgiev
 *
 */
public class Main
{
	protected static Logger logger = Logger.getLogger("FileLog");
	private static FileHandler fHandler;
	private static Socket socket;
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException
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
			connectToServer();

		} catch (IOException e)
		{
			logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private static void connectToServer()
			throws IOException, UnknownHostException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		// 192.168.0.107 (home laptop ip)
		String ipString = "";

		System.out.println("1: localhost\n2: enter ip");
		int localOrNot = Integer.parseInt(reader.readLine());
		switch (localOrNot)
		{
		case 1:
			ipString = "localhost";
			break;
		case 2:
			ipString = reader.readLine();
			break;
		}
		socket = new Socket(ipString, 21000);
		DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

		if (Authentication.clientSideAuthentication(dataInputStream, dataOutputStream))
		{
			ListenerThread listenerRunnable = new ListenerThread(dataInputStream);
			Thread listenerThread = new Thread(listenerRunnable);
			CommandsThread commandsRunnable = new CommandsThread(dataOutputStream, socket);
			Thread commandsThread = new Thread(commandsRunnable);
			listenerThread.start();
			commandsThread.start();
		} else
		{
			System.out.println("ERROR. Try again.");
			connectToServer();
		}
	}

	protected static Logger getLogger()
	{
		return logger;
	}
}