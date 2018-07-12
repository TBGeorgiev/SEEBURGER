package com.seeburger.client;

import java.io.BufferedOutputStream;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class FileSenderThread implements Runnable
{
	private Socket socket;
	private DataOutputStream dout;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	public FileSenderThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run()
	{
		try
		{
			dout = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			System.out.println("Enter the absolute path of the directory");
			String fileName = reader.readLine();
			File folder = new File(fileName);
			File[] files = folder.listFiles();
			FileInputStream in = null;
//			OutputStream out = null;
			for (File toSend : files) {
				if (!toSend.isDirectory()) {
					dout.writeUTF(toSend.getName());
					byte[] bytes = new byte[16 * 1024];
					in = new FileInputStream(toSend);
//					out = socket.getOutputStream();
					
					int count;
					while ((count = in.read(bytes)) > 0) {
//						out.write(bytes, 0, count);
						dout.write(bytes, 0, count);
					}
				}
			}
			dout.writeUTF("EOF");

//	        out.close();
	        in.close();
//	        socket.close();
			
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
