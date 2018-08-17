package com.seeburger.client;

import java.io.BufferedReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.seeburger.utilities.Authentication;
import com.seeburger.utilities.Logging;
import com.seeburger.utilities.UserManager;

public class ClientStart
{
	private static Socket socket;
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	static
	{
		try
		{
			Logging.initializeLogger("Client");
			FileHandler fHandler = new FileHandler("ClientLog.log");
			Logging.logger.addHandler(fHandler);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fHandler.setFormatter(simpleFormatter);
		} catch (SecurityException | IOException e2)
		{
			Logging.logger.log(Level.WARNING, e2.getMessage(), e2);
			e2.printStackTrace();
		}
	}

	public static void connectToServer()
	{
		// 192.168.0.107 (home laptop ip)
		String ipString = "";

		System.out.println("1: localhost\n2: enter ip");
		int localOrNot;
		try
		{
			localOrNot = Integer.parseInt(reader.readLine());
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
				while (!UserManager.userLoginOrRegisterStart(dataInputStream, dataOutputStream, reader)) {

				}
				ListenerThread listenerRunnable = new ListenerThread(dataInputStream);
				Thread listenerThread = new Thread(listenerRunnable);
				CommandsThread commandsRunnable = new CommandsThread(dataOutputStream, socket);
				Thread commandsThread = new Thread(commandsRunnable);
				listenerThread.start();
				commandsThread.start();
			} else
			{
				// TODO server side retry
				System.out.println("ERROR. Try again.");
				connectToServer();
			}
		} catch (NumberFormatException | IOException e)
		{
			// TODO Auto-generated catch block
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		} catch (InvalidKeySpecException e)
		{
			// TODO Auto-generated catch block
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
