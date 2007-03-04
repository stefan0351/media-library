package com.kiwisoft.media.show;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBErrors;
import com.kiwisoft.utils.db.DBException;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 25.04.2004
 * Time: 12:28:28
 * To change this template use File | Settings | File Templates.
 */
public class EpisodeUpdater implements Job
{
	private Collection airdates;

	public EpisodeUpdater(Collection airdates)
	{
		this.airdates=airdates;
	}

	public String getName()
	{
		return "Überprüfe Episodenreferenzen";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Überprüfe Sendetermine...");
		progressSupport.initialize(true, airdates.size(), null);
		for (Iterator it=airdates.iterator(); it.hasNext() && !progressSupport.isStoppedByUser();)
		{
			Airdate airdate=(Airdate)it.next();
			String event=airdate.getEvent();
			Show show=airdate.getShow();
			Episode episode=airdate.getEpisode();
			String episodeName=airdate.getEvent();
			boolean change=false;
			if (show!=null && event!=null && event.startsWith(show.getName()+" - \"") && event.endsWith("\""))
			{
				change=true;
				event=event.substring((show.getName()+" - \"").length(), event.length()-1);
			}
			if (episode==null && !StringUtils.isEmpty(episodeName))
			{
				if (show!=null)
				{
					try
					{
						episode=ShowManager.getInstance().getEpisodeByName(show, event);
					}
					catch (DBException e)
					{
						if (e.getErrorCode()==DBErrors.MULTIPLE_OBJECTS_FOUND)
							progressSupport.warning("Mehrere Episoden mit Titel '"+event+"' gefunden.");
						else throw e;
					}
					if (episode!=null)
					{
						change=true;
						event=null;
					}
				}
			}
			else if (airdate.getName().equals(event+" "+event))
			{
				change=true;
				event=null;
			}
			if (change)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					airdate.setEpisode(episode);
					airdate.setEvent(event);
					transaction.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					try
					{
						if (transaction!=null) transaction.rollback();
					}
					catch (SQLException e2)
					{
						e2.printStackTrace();
					}
					throw e;
				}
			}
			progressSupport.progress(1, true);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}
}
