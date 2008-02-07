package com.kiwisoft.media.download;

/**
 * @author Stefan Stiller
*/
class ParserThread extends Thread
{
	private boolean stopped;
	private DownloadProject project;

	public ParserThread(DownloadProject project)
	{
		this.project=project;
	}

	public void run()
	{
		while (!stopped)
		{
			try
			{
				WebDocument document=project.getDocumentFromQueue(WebDocument.PARSING);
				if (document!=null) document.parse();
				sleep(100);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setStopped()
	{
		stopped=true;
	}

}
