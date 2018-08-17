package com.seeburger.utilities;

public class ServerClientCommunicationMessages
{
	public static final String MASTER_PASSWORD = "ThisIsASecretPassword123";
//	public static final String MASTER_PASSWORD = "%f1^!pZ&_n+(21['|1-@7dfr";

	public static final int HELLO = 111;

	public static final int REGISTER_PLAIN = 300;
	public static final int LOGIN_PLAIN = 350;

	public static final int REGISTER_START = 310;
	public static final int LOGIN_START = 360;

	public static final int USERDATA_READY_TO_BE_SENT = 500;
	public static final int USERDATA_SENT = 550;

	public static final int REGISTRATION_SUCCESS = 600;
	public static final int REGISTRATION_FAILED = 650;
	public static final int REGISTRATION_FAILED_USER_EXISTS = 660;
	public static final int LOGIN_SUCCESS = 700;
	public static final int LOGIN_FAILED = 750;

	public static final int STATUS_OK = 50;
	public static final int STATUS_OK_SEND_SALT_AND_ITERATIONS = 100;

	public static final int STATUS_HANDSHAKE_SUCCESS = 200;
	public static final int STATUS_HANDSHAKE_FAILED = 403;

	public static final int FILE_SENT = 9999;
}
