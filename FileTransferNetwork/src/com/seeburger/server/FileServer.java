package com.seeburger.server;

import com.seeburger.utilities.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.io.*;

public class FileServer
{
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
			BufferedInputStream bis = new BufferedInputStream(clientData);

			// String choice = clientData.readUTF();
			if (choice.equalsIgnoreCase("y"))
			{
				hashStringBefore = clientData.readUTF();
			}

			String fileName = clientData.readUTF();
			OutputStream output = new FileOutputStream(fileName);
			long size = clientData.readLong();
			byte[] buffer = new byte[300];

			// TODO Make file decryption on the fly
			while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
			{
				// test for base64 decoding on the fly
				// buffer = Base64Utilities.decodedBytes(buffer);
				output.write(buffer, 0, bytesRead);
				size -= bytesRead;
			}
			// Closing the FileOutputStream handle
			output.close();

			try
			{
				Base64Utilities.decode(fileName, fileName + "_decoded");
				Files.delete(Paths.get(fileName));
				File file = new File(fileName + "_decoded");
				file.renameTo(new File(fileName));
			} catch (Exception e)
			{
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