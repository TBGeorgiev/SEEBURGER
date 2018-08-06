package com.seeburger.server;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//VM Launch Arguments (no longer needed)
//-Xms512m -Xmx4g

/**
 * Moves files from one location to another and logs the details of the moved
 * files in a log file and also displays a logger on the console. It's possible
 * to perform various tests, which include: 1: Consistency check - compares the
 * MD5 checksum of the files before and after moving. 2: Location check - checks
 * if the files have been moved to the proper location and if they are the
 * correct amount. The tests can be enabled/disabled in the initialization of
 * the Finder class in the main method below. Each operation - file moving /
 * consistency check / location check - is done by a separate thread.
 *
 * Instructions on how to use the program: 1: Insert the absolute path of a
 * directory you want to move files from. 2: Insert the absolute path of the
 * destination directory you want to move the files to. 3: If the source
 * directory is empty - the program will wait for files to arrive. After the
 * file transfer is executed - you can enter 'y' to start a new file moving
 * operation done by a separate thread or you can enter 'end' to stop the
 * program.
 */

public class Main
{
	private static volatile int selector = 0;
	private static int port = 21000;
	private static Socket socket;
	public static Logger logger = Logger.getLogger("FileLog");
	private static FileHandler fHandler;

	public static void main(String[] args)
	{
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		try
		{
			fHandler = new FileHandler("ServerLog.log");
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
			ServerSocket serverSocket = new ServerSocket(port);
			if (!serverSocket.isClosed())
			{
				displayHelloMessage();
				logger.info("Server started at: " + dateFormat.format(date));
			} else
			{
				try
				{
					System.out.println("Port " + port + " is already in use.");
					throw new IOException();
				} catch (IOException e)
				{
					Main.getLogger().log(Level.WARNING, e.getMessage(), e);
				}
			}

			while (true)
			{
				socket = serverSocket.accept();
				if (socket.isConnected())
				{
					logger.info("Client connected from: " + socket.getInetAddress() + "\n" + dateFormat.format(date));
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
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

							finder = initializeFinder(executorService, finderSelect, dataOutputStream, dataInputStream);
							try
							{
								finder.transferFiles();
							} catch (IOException e)
							{
								System.exit(0);
								e.printStackTrace();
							} catch (InterruptedException e)
							{
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
				}

			}
			// serverSocket.close();
			// executorService.shutdown();

		} catch (IOException e1)
		{
			Main.getLogger().log(Level.WARNING, e1.getMessage(), e1);
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
			return new Finder(executorService, false, false, dataOutputStream, dataInputStream, logger);
		case 2:
			selector = 1;
			return new Finder(executorService, true, false, dataOutputStream, dataInputStream, logger);
		case 3:
			selector = 2;
			return new Finder(executorService, false, true, dataOutputStream, dataInputStream, logger);

		case 4:
			selector = 3;
			return new Finder(executorService, true, true, dataOutputStream, dataInputStream, logger);

		default:
			try
			{
				dataOutputStream.writeUTF("Incorrect input. Try again.");
				choice = Integer.parseInt(dataInputStream.readUTF());
				return initializeFinder(executorService, choice, dataOutputStream, dataInputStream);
			} catch (IOException e)
			{
				Main.getLogger().log(Level.WARNING, e.getMessage(), e);
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

	public static Logger getLogger()
	{
		return logger;
	}
}