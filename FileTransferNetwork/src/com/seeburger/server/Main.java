package com.seeburger.server;

//VM Launch Arguments (no longer needed)
//-Xms512m -Xmx4g

/**
 * Moves files from one location to another and logs the details of the moved
 * files in a log file and also displays a logger on the console. It's possible
 * to perform various tests, which include: 1: Consistency check - compares the
 * MD5 checksum of the files before and after moving. 2: Location check - checks
 * if the files have been moved to the proper location and if they are the
 * correct amount. The tests can be enabled/disabled in the initialization of
 * the Finder class in the main method below. Each operation - file moving /
 * consistency check / location check - is done by a separate thread.
 *
 * Instructions on how to use the program: 
 * 1: Insert the absolute path of a
 * directory you want to move files from.
 *
 * 2: Insert the absolute path of the destination directory you want to move the
 * files to.
 *
 * 3: If the source directory is empty - the program will wait for files to
 * arrive. After the file transfer is executed - you can enter 'y' to start a
 * new file moving operation done by a separate thread or you can enter 'end' to
 * stop the program.
 */

public class Main
{
	public static void main(String[] args)
	{
		ServerStart.startServer();
	}
}