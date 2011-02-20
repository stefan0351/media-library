package com.kiwisoft.media.dataimport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.kiwisoft.utils.JobQueue;
import com.kiwisoft.utils.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.*;

/**
 * @author Stefan Stiller
 * @since 23.12.2010
 */
public class LinkCollector
{
	private final static Log log=LogFactory.getLog(LinkCollector.class);

	private static LinkCollector instance;

	private boolean started;
	private Map<Pattern, LinkHandler> handlers;
	private MyThread thread;
	private String lastData;
	private JobQueue jobs;

	public static LinkCollector getInstance()
	{
		if (instance==null) instance=new LinkCollector();
		return instance;
	}

	public LinkCollector()
	{
		this.handlers=new Hashtable<Pattern, LinkHandler>();
		this.jobs=new JobQueue();
	}

	private void fireClipboardChanged(final String content)
	{
		if (!StringUtils.isEmpty(content))
		{
			Map.Entry[] handlers=this.handlers.entrySet().toArray(new Map.Entry[this.handlers.size()]);
			for (final Map.Entry entry : handlers)
			{
				Pattern pattern=(Pattern) entry.getKey();
				final LinkHandler handler=(LinkHandler) entry.getValue();
				int pos=0;
				Matcher matcher=pattern.matcher(content);
				while (matcher.find(pos))
				{
					final String link=matcher.group();
					jobs.addJob(new Runnable()
					{
						@Override
						public void run()
						{
							handler.loadLink(link);
						}
					});
					pos=matcher.end();
				}
			}
		}
	}

	public synchronized void start()
	{
		if (!started)
		{
			started=true;
			thread=new MyThread();
			thread.start();
		}
	}

	public synchronized void stop()
	{
		if (started)
		{
			started=false;
			if (thread!=null) thread.stopped=true;
		}
	}

	public synchronized boolean isStarted()
	{
		return started;
	}

	public void addHandler(String pattern, LinkHandler handler)
	{
		this.handlers.put(Pattern.compile(pattern), handler);
	}

	private class MyThread extends Thread
	{
		private boolean stopped;

		private MyThread()
		{
			setDaemon(true);
		}

		@Override
		public void run()
		{
			try
			{
				Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
				while (!stopped)
				{
					try
					{
						Transferable contents=clipboard.getContents(this);
						String newData=(String) contents.getTransferData(DataFlavor.stringFlavor);
						if (!StringUtils.equal(newData, lastData))
						{
							fireClipboardChanged(newData);
							lastData=newData;
						}
					}
					catch (IllegalStateException e)
					{
						log.debug("Error getting clipboard content", e);
					}
					catch (Exception e)
					{
						log.error("Error getting clipboard content", e);
					}
					sleep(1000);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
