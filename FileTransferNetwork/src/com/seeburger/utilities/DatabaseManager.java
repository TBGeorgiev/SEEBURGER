package com.seeburger.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager
{
	private static final String url = "jdbc:mysql://localhost:3306/registeredusers";
	private static final String username = "woops";
	private static final String password = "123";

	public static void insertUserIntoDatabase(String usernameToRegister, String passwordToRegister,
			String emailToRegister)
	{

		System.out.println("Connecting database...");

		try (Connection connection = DriverManager.getConnection(url, username, password))
		{
			System.out.println("Database connected!");
			if (!userExists(connection, usernameToRegister, emailToRegister))
			{
				String query = "INSERT INTO users VALUES ('" + usernameToRegister + "','" + passwordToRegister + "','"
						+ emailToRegister + "')";
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				preparedStatement.execute();
				System.out.println("User " + usernameToRegister + " added to the database successfully.");
				connection.close();
			} else
			{
				System.out.println("User already exists!");
			}

		} catch (SQLException e)
		{
			// throw new IllegalStateException("Cannot connect the database!", e);
			// e.printStackTrace();
			System.out.println("SQL Exception!!");
		}

	}

	public static boolean userExists(Connection connection, String username, String email)
	{
		String userNameToCheck = "'" + username + "'";
		System.out.println(userNameToCheck);
		ResultSet rs = null;
		String query = "SELECT * FROM users WHERE username = " + userNameToCheck;
		try
		{
			PreparedStatement prest = connection.prepareStatement(query);
			rs = prest.executeQuery();
			if (rs.next()) {
				System.out.println("inside next");
				return true;
			}
		} catch (SQLException e)
		{
			// TODO: handle exception
		}
		return false;
	}
	
	

	// For testing purposes
	public static void main(String[] args)
	{
		insertUserIntoDatabase("test2", "test", "test@abv.bg");
	}

}
