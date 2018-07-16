package com.seeburger.server;

import com.seeburger.utilities.*;
import java.net.*;
import java.io.*;

public class FileServer
{
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private String hashStringBefore;

	public FileServer(Socket clientSocket, ServerSocket serverSocket)
	{
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	public void startFileServer() throws IOException
	{

		int bytesRead;

		while (true)
		{

			// clientSocket = serverSocket.accept();
			if (clientSocket.isClosed())
			{
				clientSocket = serverSocket.accept();
			}

			InputStream in = clientSocket.getInputStream();

			DataInputStream clientData = new DataInputStream(in);

			String choice = clientData.readUTF();
			if (choice.equalsIgnoreCase("y"))
			{
				hashStringBefore = clientData.readUTF();
			}

			String fileName = clientData.readUTF();
			OutputStream output = new FileOutputStream(fileName);
			long size = clientData.readLong();
			byte[] buffer = new byte[1024];
			while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
			{
				output.write(buffer, 0, bytesRead);
				size -= bytesRead;
			}
			System.out.println("File " + fileName + " uploaded successfully.");

			if (choice.equalsIgnoreCase("y"))
			{
				DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
				String newHashString = ChecksumUtilities.getMD5(new File(fileName));
				if (newHashString.compareTo(hashStringBefore) == 0)
				{
					dataOutputStream.writeUTF("\tFile hash matches.");
				} else
				{
					dataOutputStream.writeUTF("\tFile hash mismatch!\n" + hashStringBefore + "\n" + newHashString);
				}
			}
			// Closing the FileOutputStream handle
			in.close();
			clientData.close();
			output.close();
		}
	}
}