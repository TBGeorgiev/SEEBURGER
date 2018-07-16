package com.seeburger.client;

import com.seeburger.utilities.*;
import java.net.*;
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
		// TODO Auto-generated constructor stub
	}

	public void sendFile() throws IOException
	{

		System.out.println("Do you want to enable File Consistency Checks (MD5 Checksum)?\nY / N:");

		choice = scanner.nextLine();

		while (true)
		{
			if (sock.isClosed())
			{
				sock = new Socket("localhost", 21000);
			}
			// Send file
			System.out.println("Enter the file's absolute path: ");
			String filePath = scanner.nextLine();
			File myFile = new File(filePath);
			byte[] mybytearray = new byte[(int) myFile.length()];

			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			// bis.read(mybytearray, 0, mybytearray.length);

			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);

			OutputStream os = sock.getOutputStream();

			// Sending file name and file size to the server
			DataOutputStream dos = new DataOutputStream(os);

			dos.writeUTF(choice);
			if (choice.equalsIgnoreCase("y"))
			{
				fileHashString = ChecksumUtilities.getMD5(myFile);
				// System.out.println("File hash before sending: " + fileHashString);
				dos.writeUTF(fileHashString);
			}
			dos.writeUTF(myFile.getName());
			dos.writeLong(mybytearray.length);
			dos.write(mybytearray, 0, mybytearray.length);
			dos.flush();

			if (choice.equalsIgnoreCase("y"))
			{
				DataInputStream dataInputStream = new DataInputStream(sock.getInputStream());
				String hashAnswer = dataInputStream.readUTF();
				System.out.println(hashAnswer);
			}
			// Closing socket
			os.close();
			dos.close();
			System.out.println("Do you want to send more files?\ny / n:");
			String answer = scanner.nextLine();
			if (answer.equalsIgnoreCase("n"))
			{
				sock.close();
				break;
			} else if (answer.equalsIgnoreCase("y"))
			{
				continue;
			}

			// sock.close();

		}

	}
}