package com.kiwisoft.media;

import javax.servlet.ServletException;

import org.apache.jasper.runtime.HttpJspBase;

import com.kiwisoft.media.MediaManagerApp;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 19.06.2004
 * Time: 00:28:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class MediaJspBase extends HttpJspBase
{
	public void init() throws ServletException
	{
		super.init();
		MediaManagerApp.getInstance(getServletContext());
	}
}
