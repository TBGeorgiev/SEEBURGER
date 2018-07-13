package com.seeburger.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileSenderThread implements Runnable
{
	private Socket socket;
	private DataOutputStream dout;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private String FILE_TO_SEND = "c:/temp/source.pdf";

	public FileSenderThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run()
	{


		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
//	    ServerSocket servsock = null;
//	    Socket sock = null;
	    try {
//	      servsock = new ServerSocket(SOCKET_PORT);
	      while (true) {
	        System.out.println("Waiting...");
	        try {
//	          sock = servsock.accept();
	          System.out.println("Accepted connection : " + socket);
	          // send file
	          System.out.println("Enter the file's absolute path:");
	          FILE_TO_SEND = reader.readLine();
	          System.out.println("Enter the file's name:");
	          String fileName = reader.readLine();
	          File myFile = new File (FILE_TO_SEND);


	          byte [] mybytearray  = new byte [(int)myFile.length()];
	          fis = new FileInputStream(myFile);
	          bis = new BufferedInputStream(fis);
	          bis.read(mybytearray,0,mybytearray.length);
	          os = socket.getOutputStream();
	          os.write(fileName.getBytes());
	          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
	          os.write(mybytearray,0,mybytearray.length);
	          os.flush();
	          System.out.println("Done.");
	        } catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        finally {
	        	System.out.println("end");
	          if (bis != null)
				try
				{
					bis.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          if (os != null)
				try
				{
					os.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//	          if (socket!=null) socket.close();
	        }
	      }
	    }
	    finally {
	      System.out.println("finally");
	    }
//		try
//		{
//			dout = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//			System.out.println("Enter the absolute path of the directory");
//			String fileName = reader.readLine();
//			File folder = new File(fileName);
//			File[] files = folder.listFiles();
//			FileInputStream in = null;
////			OutputStream out = null;
//			for (File toSend : files) {
//				if (!toSend.isDirectory()) {
//					dout.writeUTF(toSend.getName());
//					byte[] bytes = new byte[16 * 1024];
//					in = new FileInputStream(toSend);
////					out = socket.getOutputStream();
//
//					int count;
//					while ((count = in.read(bytes)) > 0) {
////						out.write(bytes, 0, count);
//						dout.write(bytes, 0, count);
//					}
//				}
//			}
//			dout.writeUTF("EOF");
//
////	        out.close();
//	        in.close();
////	        socket.close();
//
//
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
