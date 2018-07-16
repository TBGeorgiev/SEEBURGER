package com.seeburger.server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiverThread implements Runnable
{
	private Socket clientSocket;
	private ServerSocket serverSocket;
	public final static int FILE_SIZE = 6022386;
	public String FILE_TO_RECEIVED = null;
	public static boolean toStop = false;


	public FileReceiverThread(Socket clientSocket, ServerSocket serverSocket) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}