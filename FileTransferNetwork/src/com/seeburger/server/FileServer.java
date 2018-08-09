package com.seeburger.server;

import com.seeburger.utilities.*;
import java.net.*;
import java.util.Arrays;
import java.io.*;

public class FileServer
{
	private static final int BUFFER_SIZE = 6000;
	private Socket clientSocket;
	private String hashStringBefore;
	private String choice;

	public FileServer(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}

	public void startFileServer() throws IOException
	{
		InputStream in = clientSocket.getInputStream();
		DataInputStream clientData = new DataInputStream(in);

		choice = clientData.readUTF();
		int bytesRead;

		while (true)
		{
			if (clientSocket.isClosed())
			{
				break;
			}
			if (choice.equalsIgnoreCase("y"))
			{
				hashStringBefore = clientData.readUTF();
			}
			String fileName = clientData.readUTF();
			OutputStream output = new FileOutputStream(fileName);
			byte[] buffer = new byte[BUFFER_SIZE];

			boolean end = false;
			while ((bytesRead = clientData.read(buffer)) != -1)
			{
				byte[] realBuff = Arrays.copyOf(buffer, bytesRead);
				if (realBuff[realBuff.length - 1] == -1)
				{
					realBuff = Arrays.copyOf(buffer, bytesRead - 1);
					end = true;
				}
				output.write(Base64Utilities.decodedBytes(realBuff));
				output.flush();
				if (end)
				{
					break;
				}
			}
			// Closing the FileOutputStream handle
			output.close();

			System.out.println("File " + fileName + " uploaded successfully from: " + clientSocket);
			Logging.logger.info("File " + fileName + " uploaded successfully from: " + clientSocket);

			if (choice.equalsIgnoreCase("y"))
			{
				Logging.logger.info("File hash tests on file " + fileName);
				DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
				String newHashString = ChecksumUtilities.getMD5(new File(fileName));
				if (newHashString.compareTo(hashStringBefore) == 0)
				{
					Logging.logger.info("\tFile hash matches.");
					dataOutputStream.writeUTF("\tFile hash matches.");
				} else
				{
					Logging.logger.info("\tFile hash mismatch!\n" + hashStringBefore + "\n" + newHashString);
					dataOutputStream.writeUTF("\tFile hash mismatch!\n" + hashStringBefore + "\n" + newHashString);
				}
			}
			String uploadMore = clientData.readUTF();
			if (uploadMore.equalsIgnoreCase("n"))
			{
				in.close();
				clientData.close();
				break;
			}
		}
	}
}