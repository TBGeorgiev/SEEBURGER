package com.seeburger.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import com.seeburger.utilities.EncryptionDecryptionManager;
import com.seeburger.utilities.Logging;

public class FileReceiverThread implements Runnable
{
	private Socket clientSocket;
	private ServerSocket serverSocket;

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
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
	}
}