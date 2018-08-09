package com.seeburger.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.seeburger.utilities.Authentication;
import com.seeburger.utilities.Logging;

public class ServerStart
{
	private static volatile int selector = 0;
	private static Socket socket;
	private static FileHandler fHandler;
	private static int port = 21000;

	static
	{
		Logging.initializeLogger("Server");

		try
		{
			fHandler = new FileHandler("ServerLog.log");
			Logging.logger.addHandler(fHandler);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fHandler.setFormatter(simpleFormatter);
		} catch (SecurityException | IOException e2)
		{

			Logging.logger.log(Level.WARNING, e2.getMessage(), e2);
			e2.printStackTrace();
		}
	}

	protected static void startServer()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try
		{
			ServerSocket serverSocket = new ServerSocket(port);
			if (!serverSocket.isClosed())
			{
				displayHelloMessage();
				Logging.logger.info("Server started at: " + dateFormat.format(date));
			} else
			{
				try
				{
					System.out.println("Port " + port + " is already in use.");
					throw new IOException();
				} catch (IOException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
				}
			}

			while (true)
			{
				socket = serverSocket.accept();
				if (socket.isConnected())
				{
					Logging.logger
							.info("Client connected from: " + socket.getInetAddress() + "\n" + dateFormat.format(date));
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					ExecutorService executorService = Executors.newFixedThreadPool(100);

					if (Authentication.serverSideAuthentication(dataInputStream, dataOutputStream))
					{
						dataOutputStream.writeUTF(printWelcomeMenu());
						int choice = Integer.parseInt(dataInputStream.readUTF());

						// choice 1 = file transfer inside the server
						if (choice == 1)
						{
							while (!RunnableClass.getToStop())
							{
								dataOutputStream.writeUTF(printMainMenu());
								Finder finder;
								int finderSelect = Integer.parseInt(dataInputStream.readUTF());

								finder = initializeFinder(executorService, finderSelect, dataOutputStream,
										dataInputStream);
								try
								{
									finder.transferFiles();
								} catch (IOException e)
								{
									// System.exit(0);
									Logging.logger.log(Level.WARNING, e.getMessage(), e);
									e.printStackTrace();
								} catch (InterruptedException e)
								{
									Logging.logger.log(Level.WARNING, e.getMessage(), e);
									e.printStackTrace();
								}
							}

							// choice 2 = file upload from client to server
						} else if (choice == 2)
						{
							dataOutputStream.writeUTF("exit_listener");
							FileReceiverThread fileReceiverThread = new FileReceiverThread(socket);
							executorService.execute(fileReceiverThread);
						}
					} else
					{
						System.out.println("Server refused connection for: " + socket);
						System.out.println("Waiting for new connection..");
					}

				}

			}
			// serverSocket.close();
			// executorService.shutdown();

		} catch (IOException | NumberFormatException | NoSuchAlgorithmException | InvalidKeySpecException e1)
		{
			Logging.logger.log(Level.WARNING, e1.getMessage(), e1);
			e1.printStackTrace();
		}

	}

	private static Finder initializeFinder(ExecutorService executorService, int choice,
			DataOutputStream dataOutputStream, DataInputStream dataInputStream)
	{
		switch (choice)
		{
		case 1:
			selector = 0;
			return new Finder(executorService, false, false, dataOutputStream, dataInputStream);
		case 2:
			selector = 1;
			return new Finder(executorService, true, false, dataOutputStream, dataInputStream);
		case 3:
			selector = 2;
			return new Finder(executorService, false, true, dataOutputStream, dataInputStream);

		case 4:
			selector = 3;
			return new Finder(executorService, true, true, dataOutputStream, dataInputStream);

		default:
			try
			{
				dataOutputStream.writeUTF("Incorrect input. Try again.");
				choice = Integer.parseInt(dataInputStream.readUTF());
				return initializeFinder(executorService, choice, dataOutputStream, dataInputStream);
			} catch (IOException e)
			{
				Logging.logger.log(Level.WARNING, e.getMessage(), e);
				e.printStackTrace();
			}
			break;
		}
		return null;
	}

	private static String printWelcomeMenu()
	{
		return ("1:Move files on the server.\n2:Upload files to the server.");
	}

	private static String printMainMenu()
	{
		return ("1: Transfer file with no tests.\n2: Transfer files with MD5 Checksum tests only."
				+ "\n3: Transfer files with location tests only.\n4: Transfer files with both tests enabled.");
	}

	protected static int getSelector()
	{
		return selector;
	}

	protected static void setSelector(int toSet)
	{
		selector = toSet;
	}

	private static void displayHelloMessage()
	{
		System.out.println("Server started. Port: " + port + "\nWaiting for a connection.");
	}

	protected static Socket getClientSocket()
	{
		return socket;
	}
}
