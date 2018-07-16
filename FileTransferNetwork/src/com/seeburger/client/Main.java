package com.seeburger.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Client used to send commands and receive information
 * from the server of the file transferring app.
 * @author ts.georgiev
 *
 */
public class Main {
	

	public static void main(String[] args) {
		Socket socket;
		try
		{
			socket = new Socket("localhost", 21000);
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			
			ListenerThread listenerRunnable = new ListenerThread(dataInputStream);
			Thread listenerThread = new Thread(listenerRunnable);
			CommandsThread commandsRunnable = new CommandsThread(dataOutputStream, socket);
			Thread commandsThread = new Thread(commandsRunnable);
			listenerThread.start();
			commandsThread.start();	
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}