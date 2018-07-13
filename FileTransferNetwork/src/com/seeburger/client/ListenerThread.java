package com.seeburger.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;



/**Listens for messages sent by the server
 * and displays them on the console.
 * @author ts.georgiev
 *
 */
public class ListenerThread implements Runnable {
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private Socket socket;

	public ListenerThread(DataInputStream dataInputStream, Socket socket, DataOutputStream dataOutputStream) {
		this.dataInputStream = dataInputStream;
		this.socket = socket;
		this.dataOutputStream = dataOutputStream;
	}

	@Override
	public void run() {
		String str = "";

		while (!CommandsThread.getExitStatus()) {
			System.out.println("Inside Listener Thread");
			try {
				str = dataInputStream.readUTF();
			} catch (IOException e) {
//				System.exit(0);
				e.printStackTrace();
			}
			if (str.equals("sendFile")) {
				FileSenderThread sender = new FileSenderThread(socket);
				Thread senderThread = new Thread(sender);
				senderThread.start();
				return;

			}
			else if (str.equals("moveFile")) {
				CommandsThread commandsRunnable = new CommandsThread(dataOutputStream);
				Thread commandsThread = new Thread(commandsRunnable);
				commandsThread.start();
			} else {
				System.out.println(str);
				str = new Scanner(System.in).nextLine();
				try
				{
					dataOutputStream.writeUTF(str);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}