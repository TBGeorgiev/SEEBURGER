package com.seeburger.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.seeburger.utilities.Logging;

/**
 * Thread for checking the location/destination and correct file amount.
 */

public class DestinationChecker implements Runnable
{

	private RunnableClass runnableClass;
	private DataOutputStream dout;
	private String location;
	private String destination;
	private int numberOfFiles;

	protected DestinationChecker(RunnableClass runnableClass, String location, String destination, int numberOfFiles,
			DataOutputStream dout)
	{
		this.runnableClass = runnableClass;
		this.location = location;
		this.destination = destination;
		this.numberOfFiles = numberOfFiles;
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
				Logging.logger.info("Starting location and destination tests:");
				this.dout.writeUTF("Location and destination tests:");
			} catch (IOException e)
			{
				Logging.logger.log(Level.WARNING, e.getMessage(), e);
				e.printStackTrace();
			}
			if (isLocationEmpty(this.location))
			{
				try
				{
					Logging.logger.info("\tLocation test: Good\" + \" - No files present.");
					this.dout.writeUTF("\tLocation test: Good" + " - No files present.");
				} catch (IOException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
					e.printStackTrace();
				}
			} else
			{
				try
				{
					Logging.logger
							.info("\tLocation test: Bad" + " - Files still present in location: " + this.location);
					this.dout.writeUTF("\tLocation test: Bad" + " - Files still present in location: " + this.location);
				} catch (IOException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
					e.printStackTrace();
				}
			}
			if (destinationTest(this.destination, this.numberOfFiles))
			{
				try
				{
					Logging.logger.info("\tDestination test: Good" + " - Number of files matches.");
					this.dout.writeUTF("\tDestination test: Good" + " - Number of files matches.");
				} catch (IOException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
					e.printStackTrace();
				}
			} else
			{
				try
				{
					Logging.logger.info("\tDestination test: Bad" + " - Number of files mismatch.");
					this.dout.writeUTF("\tDestination test: Bad" + " - Number of files mismatch.");
				} catch (IOException e)
				{
					Logging.logger.log(Level.WARNING, e.getMessage(), e);
					e.printStackTrace();
				}
			}
			if (ServerStart.getSelector() == 1)
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
			Logging.logger.info("Location and destination tests finished.");
			runnableClass.getLock().notifyAll();
		}

	}

	private boolean isLocationEmpty(String location)
	{
		File folder = new File(location);
		File[] files = folder.listFiles();
		for (File file : files)
		{
			if (!file.isDirectory())
			{
				return false;
			}
		}
		return true;
	}

	private boolean destinationTest(String destination, int numberOfFiles)
	{
		int count = 0;
		File folder = new File(destination);
		File[] files = folder.listFiles();
		for (File file : files)
		{
			if (!file.isDirectory())
			{
				count++;
			}
		}
		if (count == numberOfFiles)
		{
			return true;
		} else
		{
			return false;
		}
	}
}
