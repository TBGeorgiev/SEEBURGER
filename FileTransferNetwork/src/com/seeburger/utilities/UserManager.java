package com.seeburger.utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UserManager
{
	public static boolean userRegister(DataInputStream inputStream, DataOutputStream outputStream, BufferedReader reader) {
		try
		{
			outputStream.writeInt(ServerClientCommunicationMessages.REGISTER_START);
			if (inputStream.readInt() == ServerClientCommunicationMessages.STATUS_OK) {
				System.out.println("Enter a username:");
				//TODO check if username exists and verifications of symbols
				String username = reader.readLine();
				System.out.println("Enter a password:");
				//TODO password verifications
				String password = reader.readLine();
				System.out.println("Enter your email:");
				//TODO checks for email
				String email = reader.readLine();

				outputStream.writeInt(ServerClientCommunicationMessages.USERDATA_READY_TO_BE_SENT);
				if (inputStream.readInt() == ServerClientCommunicationMessages.STATUS_OK) {
					//TODO encryption
					outputStream.writeUTF(username + "#" + password + "#" + email + "#" + ServerClientCommunicationMessages.USERDATA_SENT);
//					outputStream.writeInt(ServerClientCommunicationMessages.USERDATA_SENT);
					int registrationResult = inputStream.readInt();
					if (registrationResult == ServerClientCommunicationMessages.REGISTRATION_SUCCESS) {
						System.out.println("User " + username + " registered successfully.");
						return true;
					}
					else if (registrationResult == ServerClientCommunicationMessages.REGISTRATION_FAILED) {
						System.out.println("Registration failed");
					} else {
						System.out.println("Something went horribly wrong");
					}

				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean serverRegister(DataInputStream inputStream, DataOutputStream outputStream) {
		try
		{
			if (inputStream.readInt() == ServerClientCommunicationMessages.REGISTER_START) {
				outputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
				if (inputStream.readInt() == ServerClientCommunicationMessages.USERDATA_READY_TO_BE_SENT) {
					outputStream.writeInt(ServerClientCommunicationMessages.STATUS_OK);
					String userData = inputStream.readUTF();
					String[] split = userData.split("#");
					// split[3] is the code
					if (Integer.parseInt(split[3]) == ServerClientCommunicationMessages.USERDATA_SENT) {
						//DATABASE INSERTION
						DatabaseManager.insertUserIntoDatabase(split[0], split[1], split[2]);
						outputStream.writeInt(ServerClientCommunicationMessages.REGISTRATION_SUCCESS);
					} else {
						outputStream.writeInt(ServerClientCommunicationMessages.REGISTRATION_FAILED);
					}
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
