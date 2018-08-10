package com.seeburger.client;

import com.seeburger.utilities.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.*;

public class FileClient
{
	private static final int BUFFER_SIZE = 6000;
	private Socket sock;
	private Scanner scanner = new Scanner(System.in);
	private String choice;
	private String fileHashString;

	protected FileClient(Socket sock)
	{
		this.sock = sock;
	}

	protected void sendFile() throws IOException
	{
		System.out.println("Do you want to enable File Consistency Checks (MD5 Checksum)?\nY / N:");
		choice = scanner.nextLine();

		OutputStream os = sock.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeUTF(choice);

		while (true)
		{
			System.out.println("Enter the file's absolute path: ");
			String filePath = scanner.nextLine();
			File myFile = new File(filePath);
			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);

			if (choice.equalsIgnoreCase("y"))
			{
				fileHashString = ChecksumUtilities.getMD5(myFile);
				dos.writeUTF(fileHashString);
			}

			// Sending file name to the server
			dos.writeUTF(myFile.getName());

			// Send file in encoded byte packets
			int count;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((count = dis.read(buffer)) > 0)
			{
				byte[] realBuff = Arrays.copyOf(buffer, count);
//				dos.write(Base64Utilities.encodedBytes(realBuff));
				try
				{
					byte[] encryptedBytes = EncryptionDecryptionManager.encryptBytes(realBuff);
					dos.write(encryptedBytes);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Sending -1 to mark as EOF
			dos.write(-1);

			dos.flush();
			fis.close();
			bis.close();

			if (choice.equalsIgnoreCase("y"))
			{
				DataInputStream dataInputStream = new DataInputStream(sock.getInputStream());
				String hashAnswer = dataInputStream.readUTF();
				System.out.println(hashAnswer);
			}
			System.out.println("Do you want to send more files?\ny / n:");
			String answer = scanner.nextLine();
			if (answer.equalsIgnoreCase("n"))
			{
				dos.writeUTF(answer);
				dos.flush();
				break;
			} else if (answer.equalsIgnoreCase("y"))
			{
				dos.writeUTF(answer);
				continue;
			}
		}
	}
}