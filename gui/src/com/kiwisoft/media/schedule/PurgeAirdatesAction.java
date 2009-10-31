package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.progress.SmallProgressDialog;

/**
 * @author Stefan Stiller
 */
public class PurgeAirdatesAction extends ContextAction
{
	private ApplicationFrame frame;

	public PurgeAirdatesAction(ApplicationFrame frame)
	{
		super("Purge");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		SmallProgressDialog progressDialog=new SmallProgressDialog(frame, new MyJob());
		progressDialog.start();

	}

	private static class MyJob implements Job
	{
		public MyJob()
		{
		}

		@Override
		public boolean run(ProgressListener progressListener) throws Exception
		{
			final ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
			progressSupport.startStep("Initializing...");
			final Set<Airdate> airdates=DBLoader.getInstance().loadSet(Airdate.class, null, "viewdate<date_add(now(), interval -1 month);");
			progressSupport.initialize(true, airdates.size(), null);
			progressSupport.startStep("Purging...");
			for (final Airdate airdate : airdates)
			{
				if (progressSupport.isStoppedByUser()) return false;
				if (!DBSession.execute(new Transactional()
				{
					@Override
					public void run() throws Exception
					{
						airdate.delete();
					}

					@Override
					public void handleError(Throwable throwable, boolean rollback)
					{
						progressSupport.error(throwable);
					}
				}))
				{
					return false;
				}
				progressSupport.progress();
			}

			return true;
		}

		@Override
		public String getName()
		{
			return "Purge Airdates";
		}

		@Override
		public void dispose() throws IOException
		{
		}
	}
}
