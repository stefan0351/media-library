package com.kiwisoft.media.download;

/**
 * @author Stefan Stiller
*/
class DownloadThread extends Thread
{
	private boolean stopped;
	private DownloadProject project;

	public DownloadThread(DownloadProject project)
	{
		this.project=project;
	}

	public void run()
	{
		while (!stopped)
		{
			try
			{
				WebDocument document=project.getDocumentFromQueue(WebDocument.DOWNLOADING);
				if (document!=null) document.download();
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
		this.stopped=true;
	}
}
