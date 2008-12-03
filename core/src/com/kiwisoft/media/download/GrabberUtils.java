package com.kiwisoft.media.download;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;

import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.JobQueue;

/**
 * @author Stefan Stiller
 */
public class GrabberUtils
{
	private GrabberUtils()
	{
	}

	public static File buildFile(URL url, String contentType) throws UnsupportedEncodingException
	{
		String path=url.getPath();
		if ("".equals(path)) path="/index.html";
		if (path.endsWith("/")) path=path+"index.html";

		String query=url.getQuery();
		if (query!=null)
		{
			int indexName=path.lastIndexOf("/");
			int indexExt=path.lastIndexOf(".");
			if (indexExt>0 && indexExt>indexName)
			{
				path=path.substring(0, indexExt)+"_"+URLEncoder.encode(query, "UTF-8")+path.substring(indexExt, path.length());
				path=path.replace('%', '_');
			}
			else
			{
				path=path+"_"+URLEncoder.encode(query, "UTF-8");
			}
		}

		if (contentType!=null && contentType.startsWith("text/html") && !path.toLowerCase().endsWith(".html"))
		{
			path=path+".html";
		}

		return new File(Configuration.getInstance().getString("path.downloads"), url.getHost()+File.separator+path);
	}

	public static URL getRelativeURL(URL url, String path)
	{
		try
		{
			URL newURL=new URL(url, path);
			if ("http".equalsIgnoreCase(newURL.getProtocol()))
				return newURL;
			else
				return null;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getURLWithoutRef(URL url)
	{
		String ref=url.getRef();
		String s=url.toString();
		if (ref!=null && ref.length()>0)
		{
			return s.substring(0, s.lastIndexOf("#"));
		}
		return s;
	}

	public static URL getRealURL(URL url) throws IOException
	{
		HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
		try
		{
			urlConnection.getHeaderFields(); // Causes the url to be updated
			url=urlConnection.getURL();
			return url;
		}
		finally
		{
			urlConnection.disconnect();
		}
	}

	private static JobQueue downloadQueue;

	public static JobQueue getDownloadQueue()
	{
		if (downloadQueue==null) downloadQueue=new JobQueue();
		return downloadQueue;
	}

	private static JobQueue parserQueue;

	public static JobQueue getParserQueue()
	{
		if (parserQueue==null) parserQueue=new JobQueue();
		return parserQueue;
	}

	public static void startAllQueues()
	{
		if (downloadQueue!=null) downloadQueue.start();
		if (parserQueue!=null) parserQueue.start();
	}

	public static void stopAllQueues()
	{
		if (downloadQueue!=null) downloadQueue.stop(DateUtils.MINUTE);
		if (parserQueue!=null) parserQueue.stop(DateUtils.MINUTE);
	}
}
