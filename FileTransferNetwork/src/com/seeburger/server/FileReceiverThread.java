package com.seeburger.server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class FileReceiverThread implements Runnable
{
	private Socket clientSocket;
	private DataInputStream inputStream;
	public final static int FILE_SIZE = 6022386;
	public String FILE_TO_RECEIVED = "s:/test/newFile.jpg";


	public FileReceiverThread(Socket clientSocket, DataInputStream inputStream) {
		this.clientSocket = clientSocket;
		this.inputStream = inputStream;
	}



	@Override
	public void run()
	{
		int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
//	    Socket sock = null;
	    try {
//	      sock = new Socket(SERVER, SOCKET_PORT);
	      System.out.println("Connecting...");

	      // receive file
	      byte [] mybytearray  = new byte [FILE_SIZE];
//	      InputStream is = clientSocket.getInputStream();
	      DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

	      File file = new File(FILE_TO_RECEIVED);

	      fos = new FileOutputStream(file);
	      bos = new BufferedOutputStream(fos);
	      bytesRead = dataInputStream.read(mybytearray,0,mybytearray.length);
	      current = bytesRead;
	      int count;
	      byte[] buffer = new byte[8192]; // or 4096, or more
	      while ((count = dataInputStream.read(buffer)) > 0)
	      {
	        bos.write(buffer, 0, count);
	      }
	      System.out.println("File " + FILE_TO_RECEIVED
	          + " downloaded (" + current + " bytes read)");
	    } catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally {
	      if (fos != null)
			try
			{
				fos.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	      if (bos != null)
//			try
//			{
//				bos.close();
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	      if (clientSocket != null)
//			try
//			{
//				clientSocket.close();
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    }




//        InputStream in = null;
//        OutputStream out = null;
//
//
//        try {
//            in = clientSocket.getInputStream();
//        } catch (IOException ex) {
//            System.out.println("Can't get socket input stream.");
//        }
//
//        try {
//        	String fileName;
//
//        	while(!(fileName = inputStream.readUTF()).equals("EOF")) {
//        		out = new FileOutputStream(fileName);
//        		byte[] bytes = new byte[16*1024];
//
//        		int count;
//        		try
//        		{
//        			while ((count = in.read(bytes)) > 0) {
//        				try
//        				{
//        					out.write(bytes, 0, count);
//        				} catch (IOException e)
//        				{
//        					// TODO Auto-generated catch block
//        					e.printStackTrace();
//        				}
//        			}
//        			System.out.println(fileName);
//        		} catch (IOException e1)
//        		{
//        			// TODO Auto-generated catch block
//        			e1.printStackTrace();
//        		}
////        		File folder = new File(fileName);
////        		File[] files = folder.listFiles();
////        		for (File file : files) {
////        			if (!file.isDirectory()) {
////
////        			}
////
////        		}
//
//        	}
//        	System.out.println("AFTER EOF");
//
//        } catch (IOException ex) {
//            System.out.println("File not found. ");
//        }
//
//
//        try
//		{
//        	out.close();
//        	in.close();
////			clientSocket.close();
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
