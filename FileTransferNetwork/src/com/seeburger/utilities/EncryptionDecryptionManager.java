package com.seeburger.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionDecryptionManager
{
	private static byte[] key = { 0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65,
            0x79 };
	private static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };


	public static byte[] encryptBytes(byte[] bytesToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
		return cipher.doFinal((bytesToEncrypt));
	}

	public static byte[] decryptBytes(byte[] bytesToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
	{
		//"AES/ECB/PKCS5PADDING"
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	    IvParameterSpec ivspec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
		return cipher.doFinal((bytesToEncrypt));
	}




	public static void main(String[] args)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException
	{
//		System.out.println(new String(key));
		/*byte[] encrypted = encryptBytes(new String("test").getBytes());
		System.out.println(new String(encrypted));
		byte[] decrypted = decryptBytes(encrypted);
		System.out.println(new String(decrypted));*/

		File f1 = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\1.jpg");
		InputStream in = new FileInputStream(f1);
		OutputStream out = new FileOutputStream(new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\11.jpg"));

		byte[] buf = new byte[1024];

		int res = -1;
		while ((res = in.read(buf)) != -1)
		{
			byte[] encTmp = encryptBytes(buf);
			byte[] decTmp = decryptBytes(encTmp);
			out.write(decTmp);
		}
		

		/*byte[] encrypted = encryptBytes(Files.readAllBytes(f1.toPath()));
		byte[] decrypted = decryptBytes(encrypted);
		out.write(decrypted);*/
		out.flush();
		out.close();



	}

}
