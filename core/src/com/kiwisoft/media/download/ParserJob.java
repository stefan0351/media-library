package com.kiwisoft.media.download;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class ParserJob implements Runnable, DocumentJob
{
	private WebDocument document;

	public ParserJob(WebDocument document)
	{
		this.document=document;
	}

	@Override
	public WebDocument getDocument()
	{
		return document;
	}

	@Override
	public void run()
	{
		try
		{
			if (document.isParsable())
			{
				List<URL> containedURLs=new ArrayList<URL>();
				List<URL> linkedURLs=new ArrayList<URL>();
				boolean result;
				Parser parser=ParserFactory.getParser(document.getContentType());
				if (parser!=null)
				{
					try
					{
						parser.parse(document.getFile(), document.getURL(), containedURLs, linkedURLs);
						document.setState(WebDocument.COMPLETED);
						result=true;
					}
					catch (Exception e)
					{
						document.setState(WebDocument.FAILED);
						document.setError("Exception: "+e.getClass()+": "+e.getMessage());
						result=false;
					}
				}
				else
				{
					document.setError("No parser found.");
					document.setState(WebDocument.FAILED);
					result=false;
				}
				if (result)
				{
					WebDocument newDocument;
					GrabberProject project=document.getProject();
					for (URL elementURL : containedURLs)
					{
						document.addElement(elementURL);
						if (elementURL.getHost().equals(document.getURL().getHost()))
						{
							try
							{
								elementURL=GrabberUtils.getRealURL(elementURL);
								newDocument=project.getDocumentForURL(elementURL);
								if (newDocument==null)
								{
									newDocument=project.createDocument(elementURL);
									if (newDocument.getState()==WebDocument.NEW) newDocument.enqueueForDownload();
								}
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
					for (URL linkURL : linkedURLs)
					{
						document.addLink(linkURL);
					}
				}
			}
			else
			{
				document.setState(WebDocument.COMPLETED);
			}
		}
		finally
		{
			document.setQueued(false);
		}
	}
}
