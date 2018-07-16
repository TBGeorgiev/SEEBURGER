package com.seeburger.client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import com.seeburger.server.Main;

public class FileSenderThread implements Runnable
{
	private Socket socket;

	public FileSenderThread(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run()
	{
		FileClient fileClient = new FileClient(socket);
		try
		{
			fileClient.sendFile();
		} catch (IOException e)
		{
			Main.getLogger().log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
