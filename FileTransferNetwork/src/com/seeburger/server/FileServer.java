package com.seeburger.server;

import java.net.*;     
import java.io.*;     
     
public class FileServer {  
	private Socket clientSocket;
	private ServerSocket serverSocket;
	
	public FileServer(Socket clientSocket, ServerSocket serverSocket)
	{
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
		// TODO Auto-generated constructor stub
	}
     
  public void startFileServer() throws IOException {     
       
    int bytesRead;  
    int current = 0;  
   
         
    while(true) {  
       
//        clientSocket = serverSocket.accept();
    	if (clientSocket.isClosed()) {
    		clientSocket = serverSocket.accept();
    	}
           
        InputStream in = clientSocket.getInputStream();  
           
        DataInputStream clientData = new DataInputStream(in);   
           
        String fileName = clientData.readUTF();     
        OutputStream output = new FileOutputStream(fileName);     
        long size = clientData.readLong();     
        byte[] buffer = new byte[1024];     
        while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
        {     
            output.write(buffer, 0, bytesRead);     
            size -= bytesRead;     
        }  
           
        // Closing the FileOutputStream handle
        in.close();
        clientData.close();
        output.close();  
    }
    
  }  
}  