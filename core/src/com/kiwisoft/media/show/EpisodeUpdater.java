package com.kiwisoft.media.show;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.persistence.DBException;
import com.kiwisoft.persistence.DBErrors;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
 */
public class EpisodeUpdater implements Job
{
	private Collection<Airdate> airdates;

	public EpisodeUpdater(Collection<Airdate> airdates)
	{
		this.airdates=airdates;
	}

	@Override
	public String getName()
	{
		return "Check Episode References";
	}

	@Override
	public boolean run(ProgressListener progressListener) throws Exception
	{
		ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Check Airdates...");
		progressSupport.initialize(true, airdates.size(), null);
		for (Iterator it=airdates.iterator(); it.hasNext() && !progressSupport.isStoppedByUser();)
		{
			Airdate airdate=(Airdate)it.next();
			String event=airdate.getEvent();
			Show show=airdate.getShow();
			Episode episode=airdate.getEpisode();
			String episodeName=airdate.getEvent();
			boolean change=false;
			if (show!=null && event!=null && event.startsWith(show.getTitle()+" - \"") && event.endsWith("\""))
			{
				change=true;
				event=event.substring((show.getTitle()+" - \"").length(), event.length()-1);
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
							progressSupport.warning("Multiple episodes with title '"+event+"' found.");
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

	@Override
	public void dispose() throws IOException
	{
	}
}
