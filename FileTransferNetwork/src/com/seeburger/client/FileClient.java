package com.seeburger.client;

import com.seeburger.utilities.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Scanner;
import java.io.*;

public class FileClient
{
	private Socket sock;
	private Scanner scanner = new Scanner(System.in);
	private String choice;
	private String fileHashString;

	public FileClient(Socket sock)
	{
		this.sock = sock;
	}

	public void sendFile() throws IOException
	{
		System.out.println("Do you want to enable File Consistency Checks (MD5 Checksum)?\nY / N:");

		choice = scanner.nextLine();
		OutputStream os = sock.getOutputStream();

		DataOutputStream dos = new DataOutputStream(os);

		dos.writeUTF(choice);

		while (true)
		{
			// Send file
			System.out.println("Enter the file's absolute path: ");
			String filePath = scanner.nextLine();
			File myFile = new File(filePath);
			File encodedFile = new File(filePath + "_encoded");
			// Base64Utilities.encode(filePath, filePath + "_encoded");
			Base64Utilities.encodeFile(myFile, encodedFile);
			byte[] mybytearray = new byte[(int) encodedFile.length()];

			FileInputStream fis = new FileInputStream(encodedFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			// bis.read(mybytearray, 0, mybytearray.length);

			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);

			// TODO Make file encryption on the fly

			if (choice.equalsIgnoreCase("y"))
			{
				fileHashString = ChecksumUtilities.getMD5(myFile);
				dos.writeUTF(fileHashString);
			}

			// Sending file name and file size to the server
			dos.writeUTF(myFile.getName());
			dos.writeLong(mybytearray.length);
			dos.write(mybytearray, 0, mybytearray.length);
			dos.flush();

			fis.close();
			bis.close();
			Files.delete(encodedFile.toPath());

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
				// os.close();
				// dos.close();
				// sock.close();
				break;
			} else if (answer.equalsIgnoreCase("y"))
			{
				dos.writeUTF(answer);
				continue;
			}
		}
	}
}