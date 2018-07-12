package com.seeburger.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;



/**Listens for messages sent by the server
 * and displays them on the console.
 * @author ts.georgiev
 *
 */
public class ListenerThread implements Runnable {
	private DataInputStream dataInputStream;
	Socket socket;

	public ListenerThread(DataInputStream dataInputStream, Socket socket) {
		this.dataInputStream = dataInputStream;
		this.socket = socket;
	}

	@Override
	public void run() {
		String str = "";
		while (!CommandsThread.getExitStatus()) {
			System.out.println("Inside Listener Thread");
			try {
				str = dataInputStream.readUTF();
			} catch (IOException e) {
				System.exit(0);
				e.printStackTrace();
			}
			if (str.equals("sendFile")) {
				FileSenderThread sender = new FileSenderThread(socket);
				Thread senderThread = new Thread(sender);
				senderThread.start();
			} else {
				System.out.println(str);				
			}
		}

	}

}