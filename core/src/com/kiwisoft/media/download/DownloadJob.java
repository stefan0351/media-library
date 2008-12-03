package com.kiwisoft.media.download;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

import com.kiwisoft.cfg.Configuration;

/**
 * @author Stefan Stiller
 */
public class DownloadJob implements Runnable, DocumentJob
{
	private WebDocument document;

	public DownloadJob(WebDocument document)
	{
		this.document=document;
	}

	public WebDocument getDocument()
	{
		return document;
	}

	public void run()
	{
		try
		{
			URL url=document.getURL();
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.connect();
			int responseCode=connection.getResponseCode();
			if (responseCode<400)
			{
				document.setContentType(connection.getContentType());
				document.setSize(connection.getContentLength());
				document.setExpiration(connection.getExpiration());
				document.setLastModified(connection.getLastModified());

				// Build name for local file
				File file=GrabberUtils.buildFile(url, connection.getContentType());
				document.setFile(file);
				file.getParentFile().mkdirs();

				// Download document
//					long time1=System.currentTimeMillis();
				InputStream is=connection.getInputStream();
				FileOutputStream fos=new FileOutputStream(file);
				byte[] buffer=new byte[Configuration.getInstance().getLong("buffer.download", 4096L).intValue()];
				int bytesRead;
				while ((bytesRead=is.read(buffer))!=-1)
				{
					fos.write(buffer, 0, bytesRead);
				}
				if ("text/html".equals(document.getContentType())) fos.write(("<!-- saved form url="+url+"-->").getBytes());
				fos.flush();
				fos.close();
				is.close();
//					long time2=System.currentTimeMillis();
//					System.out.println("\tDownload Time: "+(time2-time1));
				document.setState(WebDocument.DOWNLOADED);
				document.enqueueForParsing();

				// Close connection
				connection.disconnect();
				return;
			}
			else
			{
				document.setState(WebDocument.FAILED);
				document.setError("HTTP Error: "+responseCode);
				connection.disconnect();
				return;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			document.setState(WebDocument.FAILED);
			document.setError("Exception: "+e.getClass()+": "+e.getMessage());
			return;
		}
		finally
		{
			document.setQueued(false);
		}
	}
}
