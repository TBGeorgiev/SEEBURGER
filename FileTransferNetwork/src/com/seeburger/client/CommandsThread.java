package com.seeburger.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Thread used for sending messages to the server until the 'end' command is
 * given.
 * 
 * @author ts.georgiev
 *
 */
public class CommandsThread implements Runnable
{
	private DataOutputStream dataOutputStream;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static volatile boolean toExit;
	private Socket socket;

	public CommandsThread(DataOutputStream dataOutputStream, Socket socket)
	{
		this.dataOutputStream = dataOutputStream;
		this.socket = socket;
	}

	@Override
	public void run()
	{
		String str = "";
		while (!str.equals("end"))
		{
			try
			{
				str = reader.readLine();
				if (str.equals("2"))
				{
					dataOutputStream.writeUTF("2");
					FileSenderThread fileSenderThread = new FileSenderThread(socket);
					Thread fileSender = new Thread(fileSenderThread);
					fileSender.start();
					break;
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				dataOutputStream.writeUTF(str);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				dataOutputStream.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Exiting commands thread..");
		toExit = true;
	}

	public static boolean getExitStatus()
	{
		return toExit;
	}

}