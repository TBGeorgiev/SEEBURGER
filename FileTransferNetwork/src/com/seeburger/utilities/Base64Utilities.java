package com.seeburger.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public final class Base64Utilities
{
	private static final int BUFFER_SIZE = 300; // 300 seems to be the magic number

	public static byte[] encodedBytes(byte[] bytesToEncode)
	{
		return Base64.getEncoder().encode(bytesToEncode);
	}

	public static byte[] decodedBytes(byte[] bytesToDecode)
	{
		return Base64.getDecoder().decode(bytesToDecode);
	}

	public static void encode(String sourceFile, String targetFile) throws Exception
	{
		writeEncodedByteArraysToFile(sourceFile, targetFile);
	}

	public static void decode(String sourceFile, String targetFile) throws Exception
	{
		writeDecodedByteArraysToFile(sourceFile, targetFile);
	}

	public static void writeEncodedByteArraysToFile(String sourceFile, String targetFileArg) throws IOException
	{
		File file = new File(sourceFile);
		File targetFile = new File(targetFileArg);
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(targetFile));

		int read = -1;
		byte[] bytez = new byte[BUFFER_SIZE];
		try
		{
			while ((read = reader.read(bytez)) != -1)
			{
				byte[] realBuff = Arrays.copyOf(bytez, read);
				byte[] decodedBytes = Base64.getEncoder().encode(realBuff);
				writer.write(decodedBytes);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
		reader.close();
	}

	public static void writeDecodedByteArraysToFile(String sourceFile, String targetFileArg) throws IOException
	{
		File file = new File(sourceFile);
		File targetFile = new File(targetFileArg);

		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(targetFile));

		int read = -1;
		byte[] bytez = new byte[BUFFER_SIZE];
		try
		{
			while ((read = reader.read(bytez)) != -1)
			{
				byte[] realBuff = Arrays.copyOf(bytez, read);
				byte[] decodedBytes = Base64.getDecoder().decode(realBuff);
				writer.write(decodedBytes);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
		reader.close();

	}

	public static long encodeFile(File toEncode, File encodedFile) throws IOException
	{
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(toEncode));
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(encodedFile));

		int read = -1;
		byte[] bytez = new byte[BUFFER_SIZE];
		try
		{
			while ((read = reader.read(bytez)) != -1)
			{
				byte[] realBuff = Arrays.copyOf(bytez, read);
				byte[] decodedBytes = Base64.getEncoder().encode(realBuff);
				writer.write(decodedBytes);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		writer.flush();
		writer.close();
		reader.close();

		return encodedFile.length();
	}

	/**
	 * This method writes byte array content into a file.
	 *
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException
	{

		File file = new File(fileName);
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws Exception
	{
		// File f = new File("C:\\tmp\\changelog.pdf");
		encode("C:\\Users\\ts.georgiev\\Documents\\New Version\\new again lol\\testFile\\2\\testFile.file",
				"C:\\Users\\ts.georgiev\\Documents\\New Version\\new again lol\\testFile\\2\\testFileE.file");
		decode("C:\\Users\\ts.georgiev\\Documents\\New Version\\new again lol\\testFile\\2\\testFileE.file",
				"testFileD.file");
	}

	public static byte[] loadFileAsBytesArray(String fileName) throws Exception
	{

		File file = new File(fileName);
		int length = (int) file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));

		// TODO test with 1024, original was - length
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();
		return bytes;
	}
}
