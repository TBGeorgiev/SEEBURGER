package com.seeburger.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class FileReceiverThread implements Runnable
{
	private Socket clientSocket;
	private ServerSocket serverSocket;
	public final static int FILE_SIZE = 6022386;
	public String FILE_TO_RECEIVED = null;
	public static boolean toStop = false;

	public FileReceiverThread(Socket clientSocket, ServerSocket serverSocket)
	{
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run()
	{
		FileServer fileServer = new FileServer(clientSocket, serverSocket);
		try
		{
			fileServer.startFileServer();
		} catch (IOException e)
		{
			Main.getLogger().log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
	}
}