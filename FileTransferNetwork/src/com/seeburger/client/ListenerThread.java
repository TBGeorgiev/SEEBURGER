package com.seeburger.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import com.seeburger.server.Main;



/**Listens for messages sent by the server
 * and displays them on the console.
 * @author ts.georgiev
 *
 */
public class ListenerThread implements Runnable {
	private DataInputStream dataInputStream;

	public ListenerThread(DataInputStream dataInputStream) {
		this.dataInputStream = dataInputStream;
	}

	@Override
	public void run() {
		String str = "";
		while (!CommandsThread.getExitStatus()) {
			try {
				str = dataInputStream.readUTF();
				if (str.equals("exit_listener")) {
					break;
				}
			} catch (IOException e) {
//				System.exit(0);
				Main.getLogger().log(Level.WARNING, e.getMessage(), e);
				e.printStackTrace();
			}
			System.out.println(str);
		}
//		System.out.println("Exiting listener thread..");
	}
}