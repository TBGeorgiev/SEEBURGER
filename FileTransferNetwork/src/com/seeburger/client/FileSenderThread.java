package com.seeburger.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class FileSenderThread implements Runnable {
	private Socket socket;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private String FILE_TO_SEND = null;

	public FileSenderThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		
		
		FileClient fileClient = new FileClient(socket);
		try
		{
			fileClient.sendFile();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
