package com.kiwisoft.media;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler
{
	private final static Log log=LogFactory.getLog(ExceptionHandler.class);

	public void uncaughtException(Thread t, Throwable e)
	{
		log.error("Uncaught exception", e);
		Throwable rootCause=Utils.getRootCause(e);
		StringBuilder message=new StringBuilder(e.getClass().getSimpleName()+": "+e.getMessage());
		if (rootCause!=e)
			message.append("\n\tRoot cause: ").append(rootCause.getClass().getSimpleName()).append(": ").append(rootCause.getMessage());
		JOptionPane.showMessageDialog(null, message.toString(), "Uncaught exception", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Call by EventDispatchThread in case of an exception
	 */
	public void handle(Throwable t)
	{
		uncaughtException(null, t);
	}

	public static void init()
	{
		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
	}
}
