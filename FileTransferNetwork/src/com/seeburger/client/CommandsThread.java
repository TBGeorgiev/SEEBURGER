package com.seeburger.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * Thread used for sending messages to the server
 * until the 'end' command is given.
 * @author ts.georgiev
 *
 */
public class CommandsThread implements Runnable {
	private DataOutputStream dataOutputStream;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static volatile boolean toExit;

	public CommandsThread(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

	@Override
	public void run() {
		String str = "";
		while (!str.equals("end")) {
			try {
				str = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dataOutputStream.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		toExit = true;
	}
	
	public static void setExitStatus(boolean setExit) {
		toExit = setExit;
	}
	
	public static boolean getExitStatus() {
		return toExit;
	}

}