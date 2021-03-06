package com.seeburger.server;

import com.seeburger.utilities.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Thread which performs the file consistency checks (MD5 checksum) for the
 * files before and after moving.
 */

public class ConsistencyChecker implements Runnable
{
	private RunnableClass runnableClass;
	private DataOutputStream dout;
	private LinkedHashMap<String, String> bytesList;
	private String destinationString;

	protected ConsistencyChecker(String destinationString, RunnableClass runnableClass,
			LinkedHashMap<String, String> fileByteStrings, DataOutputStream dout)
	{
		this.runnableClass = runnableClass;
		this.destinationString = destinationString;
		this.bytesList = fileByteStrings;
		this.dout = dout;
	}

	@Override
	public void run()
	{
		synchronized (runnableClass.getLock())
		{
			while (!runnableClass.isMovingFinished())
			{
				try
				{
					runnableClass.getLock().wait();
				} catch (InterruptedException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
					e.printStackTrace();
				}
			}
			try
			{
				checkFiles(destinationString, bytesList);
			} catch (Exception e)
			{
				Logging.logger.log(Level.WARNING, e.getMessage(), e);
				e.printStackTrace();
			}
			if (ServerStart.getSelector() >= 2)
			{
				try
				{
					dout.writeUTF("Start a new operation?\nYes - 'y' Close application - 'end'");
				} catch (IOException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
					e.printStackTrace();
				}
			}
			runnableClass.getLock().notifyAll();
		}
	}

	private void checkFiles(String destinationString, LinkedHashMap<String, String> bytesList) throws Exception
	{
		ArrayList<String> sourceByteStrings = new ArrayList<String>();
		File folder = new File(destinationString);
		File[] files = folder.listFiles();
		for (File file : files)
		{
			if (!file.isDirectory())
			{
				String result = ChecksumUtilities.getMD5(file);
				sourceByteStrings.add(result);
			}
		}

		int index = 0;
		Logging.logger.info("Starting file integrity tests (MD5 checksum);");
		this.dout.writeUTF("File integrity tests:");
		for (Map.Entry<String, String> map : bytesList.entrySet())
		{
			if (map.getValue().equals(sourceByteStrings.get(index)))
			{
				this.dout.writeUTF("\tMatching file: " + map.getKey());
				Logging.logger.info("\tMatching file: " + map.getKey());
			} else
			{
				this.dout.writeUTF("\tMismatching file: " + map.getKey());
				Logging.logger.info("\tMismatching file: " + map.getKey());
			}
			index++;
		}
		Logging.logger.info("File integrity tests (MD5 checksum) finished.");
	}
}