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
import java.util.logging.Logger;

/**
 * Moves files from one location to another
 * and logs the details of the moved files in
 * a log file and also displays a logger on the console.
 * It's possible to perform various tests, which include:
 * 1: Consistency check - compares the MD5
 * checksum of the files before and after moving.
 * 2: Location check - checks if the files have been moved
 * to the proper location and if they are the correct amount.
 * The tests can be enabled/disabled in the initialization of the 
 * Finder class in the main method below.
 * Each operation - file moving / consistency check / location check - 
 * is done by a separate thread.
 * 
 * Instructions on how to use the program:
 * 1: Insert the absolute path of a directory you want to move files from.
 * 2: Insert the absolute path of the destination directory you want to move the files to.
 * 3: If the source directory is empty - the program will wait for files to arrive. 
 * After the file transfer is executed - you can enter 'y' to start a new file moving operation
 * done by a separate thread or you can enter 'end' to stop the program.
 */

public class Main
{
	private static volatile int selector = 0;
	private static int port = 21000;
	private static Socket socket;
	private static Logger logger = Logger.getLogger("FileLog");
	
	public static void main(String[] args)
	{
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		//first boolean is for file integrity tests (MD5 checksum test)
		//second boolean is for location and destination tests
		try
		{
			ServerSocket serverSocket = new ServerSocket(port);
			if (!serverSocket.isClosed()) {
				displayHelloMessage();
				logger.info("Server started at: " + dateFormat.format(date));
			} else {
				try {
					System.out.println("Port " + port + " is already in use.");
					throw new IOException();					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			while (!RunnableClass.getToStop())
			{
				socket = serverSocket.accept();
				if (socket.isConnected()) {
					logger.info("Client connected from: " + socket.getInetAddress() + "\n" + dateFormat.format(date));
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					dataOutputStream.writeUTF(printWelcomeMenu());
					int toUpload = Integer.parseInt(dataInputStream.readUTF());
					switch (toUpload) {
					case 1:
						dataOutputStream.writeUTF(printMainMenu());
						Finder finder;
						int choice = Integer.parseInt(dataInputStream.readUTF());
						
						finder = initializeFinder(executorService, choice, dataOutputStream, dataInputStream);
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
						break;
						
					case 2:
						dataOutputStream.writeUTF("sendFile");			
						FileReceiverThread receiver = new FileReceiverThread(socket, dataInputStream);
						executorService.execute(receiver);
						break;					
					}
					
				}
			}
			serverSocket.close();
			executorService.shutdown();
			
						
		} catch (IOException e1)
		{
			System.exit(0);
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	private static Finder initializeFinder(ExecutorService executorService, int choice, DataOutputStream dataOutputStream, DataInputStream dataInputStream)
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
//				break;
			} catch (IOException e)
			{
				System.exit(0);
				e.printStackTrace();
			}
			break;
		}
		return null;
	}
	
	private static String printWelcomeMenu() {
		return("1:Move files on the server.\n2:Upload files to the server.");
	}
	
	private static String printMainMenu() {
		return("1: Transfer file with no tests.\n2: Transfer files with MD5 Checksum tests only."
				+ "\n3: Transfer files with location tests only.\n4: Transfer files with both tests enabled.");
	}
	
	public static int getSelector() {
		return selector;
	}
	
	public static void setSelector(int toSet) {
		selector = toSet;
	}
	
	private static void displayHelloMessage() {
		System.out.println("Server started. Port: " + port + "\nWaiting for a connection.");
	}
	
	
	public static Socket getClientSocket() {
		return socket;
	}
	
	public static Logger getLogger() {
		return logger;
	}
}
