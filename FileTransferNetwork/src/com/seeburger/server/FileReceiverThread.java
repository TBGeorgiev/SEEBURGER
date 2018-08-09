package com.seeburger.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import com.seeburger.utilities.Logging;

public class FileReceiverThread implements Runnable
{
	private Socket clientSocket;

	public FileReceiverThread(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}

	@Override
	public void run()
	{
		FileServer fileServer = new FileServer(clientSocket);
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