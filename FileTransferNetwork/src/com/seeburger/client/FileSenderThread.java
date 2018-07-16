package com.seeburger.client;

import java.io.IOException;
import java.net.Socket;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
