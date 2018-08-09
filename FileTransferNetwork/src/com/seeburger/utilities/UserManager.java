package com.seeburger.utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
		}
		return false;
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
					// DATABASE INSERTION
					if (!DatabaseManager.insertUserIntoDatabase(outputStream, split[0], split[1], split[2]))
					{
						return serverRegister(inputStream, outputStream);
					}
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
				System.out.println("Enter your password:");
				String password = reader.readLine();
				if (DatabaseManager.attemptLoginOnUser(username, password)) {
					System.out.println("LOGIN SUCCESS");
					outputStream.writeInt(ServerClientCommunicationMessages.LOGIN_SUCCESS);
					return true;
				}	
				outputStream.writeInt(ServerClientCommunicationMessages.LOGIN_FAILED);
			}		
		} catch (IOException e)
		{
			Logging.logger.log(Level.WARNING, e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Login failed. User does not exist.");
		return false;
		
	}
	
	public static boolean serverLogin(DataInputStream inputStream, DataOutputStream outputStream) {
		try
		{
			if (inputStream.readInt() == ServerClientCommunicationMessages.LOGIN_START) {
				outputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
				if (inputStream.readInt() == ServerClientCommunicationMessages.LOGIN_SUCCESS) {
					return true;
				}
				else if (inputStream.readInt() == ServerClientCommunicationMessages.LOGIN_FAILED) {
					return false;
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
