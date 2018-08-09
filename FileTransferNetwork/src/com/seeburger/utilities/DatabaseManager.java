package com.seeburger.utilities;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseManager
{
	private static final String url = "jdbc:mysql://localhost:3306/registeredusers";
	private static final String username = "woops";
	private static final String password = "123";
	private static Connection connection;

	static
	{
		try
		{
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Database connected!");
		} catch (SQLException e)
		{
			Logging.logger.log(Level.WARNING, e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean insertUserIntoDatabase(DataOutputStream outputStream, String usernameToRegister,
			String passwordToRegister, String emailToRegister) throws IOException
	{

		if (!userExists(connection, usernameToRegister, emailToRegister))
		{
			String query = "INSERT INTO users VALUES (?,?,?)";
			PreparedStatement preparedStatement;
			try
			{
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, usernameToRegister);
				preparedStatement.setString(2, passwordToRegister);
				preparedStatement.setString(3, emailToRegister);
				preparedStatement.execute();
				System.out.println("User " + usernameToRegister + " added to the database successfully.");
				outputStream.writeInt(ServerClientCommunicationMessages.REGISTRATION_SUCCESS);
				connection.close();
				return true;
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				Logging.logger.log(Level.WARNING, e.getMessage());
				e.printStackTrace();
			}
		} else
		{
			System.out.println("User already exists!");
			outputStream.writeInt(ServerClientCommunicationMessages.REGISTRATION_FAILED_USER_EXISTS);

		}
		return false;
	}

	public static boolean userExists(Connection connection, String username, String email)
	{
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String queryForUsername = "SELECT * FROM users WHERE username = ?";
		String queryForEmail = "SELECT * FROM users WHERE email = ?";

		try
		{
			PreparedStatement prestUser = connection.prepareStatement(queryForUsername);
			prestUser.setString(1, username);
			rs1 = prestUser.executeQuery();
			if (rs1.next())
			{
				return true;
			}
			PreparedStatement prestEmail = connection.prepareStatement(queryForEmail);
			prestEmail.setString(1, email);
			rs2 = prestEmail.executeQuery();
			if (rs2.next())
			{
				return true;
			}

		} catch (SQLException e)
		{
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			// TODO: handle exception
		}
		return false;
	}

	public static boolean attemptLoginOnUser(String username, String password)
	{
		ResultSet rs1 = null;
		;
		String queryForUsernameAndPass = "SELECT * FROM users WHERE username = ? AND pass = ?";

		try
		{
			PreparedStatement prestUser = connection.prepareStatement(queryForUsernameAndPass);
			prestUser.setString(1, username);
			prestUser.setString(2, password);
			rs1 = prestUser.executeQuery();
			if (rs1.next())
			{
				return true;
			}

		} catch (SQLException e)
		{
			Logging.logger.log(Level.WARNING, e.getMessage(), e);
			// TODO: handle exception
		}
		return false;
	}

	// For testing purposes
	public static void main(String[] args)
	{
		// insertUserIntoDatabase("test3", "test", "test@abv.bg");
	}

}
