package com.seeburger.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class Base64Utilities {
	
	
	public static byte[] encodedBytes(byte[] bytesToEncode) {
		return Base64.getEncoder().encode(bytesToEncode);	
	}
	
	public static byte[] decodedBytes(byte[] bytesToDecode) {
		return Base64.getDecoder().decode(bytesToDecode);
	}
	
	
	
	public static void encode(String sourceFile, String targetFile) throws Exception {
		 
        byte[] base64EncodedData = Base64.getEncoder().encode(loadFileAsBytesArray(sourceFile));
 
        writeByteArraysToFile(targetFile, base64EncodedData);
    }
 
    public static void decode(String sourceFile, String targetFile) throws Exception {
 
        byte[] decodedBytes = Base64.getDecoder().decode(loadFileAsBytesArray(sourceFile));
 
        writeByteArraysToFile(targetFile, decodedBytes);
    }
	
	public static byte[] loadFileAsBytesArray(String fileName) throws Exception {
		 
        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
 
    }
 
    /**
     * This method writes byte array content into a file.
     * 
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {
 
        File file = new File(fileName);
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
        writer.write(content);
        writer.flush();
        writer.close();
 
    }
}
