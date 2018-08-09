package com.seeburger.client;

/**
 * Client used to send commands and receive information from the server of the
 * file transferring application.
 *
 * @author ts.georgiev
 *
 */
public class Main
{
	public static void main(String[] args)
	{
		ClientStart.connectToServer();
	}
}