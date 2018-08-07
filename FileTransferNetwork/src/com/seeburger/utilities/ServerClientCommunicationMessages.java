package com.seeburger.utilities;

public class ServerClientCommunicationMessages
{
	public static final int HELLO = 111;

	public static final int REGISTER_PLAIN = 300;
	public static final int LOGIN_PLAIN = 350;

	public static final int STATUS_OK = 200;
	public static final int STATUS_OK_SEND_SALT_AND_ITERATIONS = 100;
	public static final int STATUS_WRONG_INFO = 403;

	public static final String MASTER_PASSWORD = "12345";
}
