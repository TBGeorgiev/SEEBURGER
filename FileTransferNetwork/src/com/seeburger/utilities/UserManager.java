package com.seeburger.utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

public class UserManager
{
	public static boolean userRegister(DataInputStream inputStream, DataOutputStream outputStream,
			BufferedReader reader)
	{
		try
		{
			outputStream.writeInt(ServerClientCommunicationMessages.REGISTER_START);
			if (inputStream.readInt() == ServerClientCommunicationMessages.STATUS_OK)
			{
				System.out.println("Enter a username:");
				// TODO check if username exists and verifications of symbols
				String username = reader.readLine();
				System.out.println("Enter a password:");
				// TODO password verifications
				String password = reader.readLine();
				// TODO user login needs to be fixed
				password = HashingManager.generateHashedPass(password);
				System.out.println(password);
				System.out.println("Enter your email:");
				// TODO checks for email
				String email = reader.readLine();

				outputStream.writeInt(ServerClientCommunicationMessages.USERDATA_READY_TO_BE_SENT);
				if (inputStream.readInt() == ServerClientCommunicationMessages.STATUS_OK)
				{
					// TODO encryption
					outputStream.writeUTF(username + "#" + password + "#" + email + "#"
							+ ServerClientCommunicationMessages.USERDATA_SENT);
					int registrationResult = inputStream.readInt();
					if (registrationResult == ServerClientCommunicationMessages.REGISTRATION_SUCCESS)
					{
						System.out.println("User " + username + " registered successfully.");
						return true;
					} else if (registrationResult == ServerClientCommunicationMessages.REGISTRATION_FAILED_USER_EXISTS)
					{
						System.out.println("User already exists!");
						return userRegister(inputStream, outputStream, reader);
					} else
					{
						System.out.println("Something went horribly wrong");
					}

				}
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			// TODO Auto-generated catch block
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean serverLoginOrRegisterStart(DataInputStream inputStream, DataOutputStream outputStream) {
		if (Authentication.getLogin()) {
			return serverLogin(inputStream, outputStream);
		} else {
			return serverRegister(inputStream, outputStream);
		}
	}
	
	public static boolean userLoginOrRegisterStart(DataInputStream inputStream, DataOutputStream outputStream, BufferedReader reader) {
		if (Authentication.getLogin()) {
			return userLogin(inputStream, outputStream, reader);
		} else {
			return userRegister(inputStream, outputStream, reader);
		}
	}

	public static boolean serverRegister(DataInputStream inputStream, DataOutputStream outputStream)
	{
		try
		{
			if (inputStream.readInt() == ServerClientCommunicationMessages.REGISTER_START)
			{
				outputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
				if (inputStream.readInt() == ServerClientCommunicationMessages.USERDATA_READY_TO_BE_SENT)
				{
					outputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
					String userData = inputStream.readUTF();
					// split[3] is the code
					String[] split = userData.split("#");
					String username = split[0];
					String password = split[1];
					String email = split[2];
					// DATABASE INSERTION
					if (!DatabaseManager.insertUserIntoDatabase(username, password, email))
					{
						outputStream.writeInt(ServerClientCommunicationMessages.REGISTRATION_FAILED_USER_EXISTS);
						return serverRegister(inputStream, outputStream);
					}
					outputStream.writeInt(ServerClientCommunicationMessages.REGISTRATION_SUCCESS);
					return true;
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			e.printStackTrace();
		}

		return false;
	}
	
	public static boolean userLogin(DataInputStream inputStream, DataOutputStream outputStream, BufferedReader reader) {
		try
		{
			outputStream.writeInt(ServerClientCommunicationMessages.LOGIN_START);
			if (inputStream.readInt() == ServerClientCommunicationMessages.STATUS_OK) {
				System.out.println("Enter your username:");
				String username = reader.readLine();
				outputStream.writeUTF(username);
				System.out.println("Enter your password:");
				String password = reader.readLine();
				password = HashingManager.generateHashedPass(password);
				outputStream.writeUTF(password);
				int loginResponse = inputStream.readInt();
				if (loginResponse == ServerClientCommunicationMessages.LOGIN_SUCCESS) {
					System.out.println("User logged in.");
					return true;
				}
				else if (loginResponse == ServerClientCommunicationMessages.LOGIN_FAILED) {
					System.out.println("Login failed. User does not exist.");				
				}
			}		
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			Logging.logger.log(Level.WARNING, e.getMessage());
			e.printStackTrace();
		}
		return false;
		
	}
	
	public static boolean serverLogin(DataInputStream inputStream, DataOutputStream outputStream) {
		try
		{
			if (inputStream.readInt() == ServerClientCommunicationMessages.LOGIN_START) {
				outputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
				String username = inputStream.readUTF();
				String password = inputStream.readUTF();
				if (DatabaseManager.attemptLoginOnUser(username, password)) {
					System.out.println("LOGIN SUCCESS");
					outputStream.writeInt(ServerClientCommunicationMessages.LOGIN_SUCCESS);
					return true;
				}
				System.out.println("Sending login failed response to client");
				outputStream.writeInt(ServerClientCommunicationMessages.LOGIN_FAILED);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			Logging.logger.log(Level.WARNING, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}
