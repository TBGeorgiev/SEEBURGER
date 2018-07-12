package com.seeburger.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileReceiverThread implements Runnable
{
	Socket clientSocket;
	DataInputStream inputStream;
	
	
	public FileReceiverThread(Socket clientSocket, DataInputStream inputStream) {
		this.clientSocket = clientSocket;
		this.inputStream = inputStream;
	}

	@Override
	public void run()
	{
        InputStream in = null;
        OutputStream out = null;

        
        try {
            in = clientSocket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream.");
        }

        try {
        	String fileName;
        	
        	while(!(fileName = inputStream.readUTF()).equals("EOF")) {
        		out = new FileOutputStream(fileName);
        		byte[] bytes = new byte[16*1024];
        		
        		int count;
        		try
        		{
        			while ((count = in.read(bytes)) > 0) {
        				try
        				{
        					out.write(bytes, 0, count);
        				} catch (IOException e)
        				{
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        			}
        			System.out.println(fileName);
        		} catch (IOException e1)
        		{
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
//        		File folder = new File(fileName);
//        		File[] files = folder.listFiles();
//        		for (File file : files) {
//        			if (!file.isDirectory()) {
//        				
//        			}
//        			
//        		}
        		
        	}
        	System.out.println("AFTER EOF");
        	
        } catch (IOException ex) {
            System.out.println("File not found. ");
        }


        try
		{
        	out.close();
        	in.close();
//			clientSocket.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
