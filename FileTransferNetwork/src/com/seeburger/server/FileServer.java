package com.seeburger.server;

import com.seeburger.utilities.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.*;

public class FileServer
{
	private int BUFFER_SIZE = 1387;
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private String hashStringBefore;
	private String choice;

	public FileServer(Socket clientSocket, ServerSocket serverSocket)
	{
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
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

			try
			{
				EncryptionDecryptionManager.decryptFileAndWriteInChunks(clientData, output);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
					| InvalidAlgorithmParameterException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			BUFFER_SIZE = clientData.readInt();
//			System.out.println(BUFFER_SIZE);
//			byte[] buffer = new byte[BUFFER_SIZE];
//			System.out.println(buffer.length);
//
//
//			boolean end = false;
//
//			while ((bytesRead = clientData.read(buffer)) > 0)
//			{
//				byte[] realBuff = Arrays.copyOf(buffer, bytesRead);
//				System.out.println(realBuff[0]);
//				if (realBuff[realBuff.length - 1] == -1)
//				{
//					realBuff = Arrays.copyOf(buffer, bytesRead - 1);
//					end = true;
//				}
//				try
//				{
////					byte[] decodedEncryptedBytes = Base64Utilities.decodedBytes(realBuff);
//					byte[] decryptedBytes = EncryptionDecryptionManager.decryptBytes(realBuff);
//
//					output.write(decryptedBytes);
//				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
//						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
////				output.write(Base64Utilities.decodedBytes(realBuff));
//				output.flush();
//				if (end)
//				{
//					break;
//				}
//			}
//			// Closing the FileOutputStream handle
			output.close();


			System.out.println("File " + fileName + " uploaded successfully from: " + clientSocket);
			Logging.logger.info("File " + fileName + " uploaded successfully from: " + clientSocket);

			if (choice.equalsIgnoreCase("y"))
			{
				Logging.logger.info("File hash tests on file " + fileName);
//				if (clientSocket.isClosed()) {
//					clientSocket = serverSocket.accept();
//				}
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