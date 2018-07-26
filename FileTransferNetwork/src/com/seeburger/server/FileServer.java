package com.seeburger.server;

import com.seeburger.utilities.*;
import java.net.*;
import java.util.Base64;
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
			byte[] buffer = new byte[(int) size];
			while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
			{
				//test for base64 decoding on the fly
//				buffer = Base64Utilities.decodedBytes(buffer);
				output.write(buffer, 0, bytesRead);
				size -= bytesRead;
			}
			try {
				Base64Utilities.decode(fileName, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("File " + fileName + " uploaded successfully from: " + clientSocket);
			Main.getLogger().info("File " + fileName + " uploaded successfully from: " + clientSocket);


			if (choice.equalsIgnoreCase("y"))
			{
				Main.getLogger().info("File hash tests on file " + fileName);
				DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
				String newHashString = ChecksumUtilities.getMD5(new File(fileName));
				if (newHashString.compareTo(hashStringBefore) == 0)
				{
					Main.getLogger().info("\tFile hash matches.");
					dataOutputStream.writeUTF("\tFile hash matches.");
				} else
				{
					Main.getLogger().info("\tFile hash mismatch!\n" + hashStringBefore + "\n" + newHashString);
					dataOutputStream.writeUTF("\tFile hash mismatch!\n" + hashStringBefore + "\n" + newHashString);
				}
			} else {
				in.close();
				clientData.close();
			}
			// Closing the FileOutputStream handle
			output.close();
		}
	}
}