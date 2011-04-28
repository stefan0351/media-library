package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.dataimport.movie.LoadMovieJob;
import com.kiwisoft.media.dataimport.movie.MovieDataDetailsView;
import com.kiwisoft.swing.progress.JobFinishListener;
import com.kiwisoft.swing.progress.SmallProgressDialog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/**
 * @author Stefan Stiller
 * @since 27.02.11
 */
public class IMDbLinkHandler implements LinkHandler
{
	private final static Log log=LogFactory.getLog(FanFictionNetLinkHandler.class);

	@Override
	public void loadLink(String url)
	{
		log.info("IMDb URL found in clipboard: "+url);
		try
		{
			IMDbComLoader loader=IMDbComLoader.create(url);
			if (loader!=null)
			{
				final LoadMovieJob job=new LoadMovieJob(loader);
				new SmallProgressDialog(null, job).start(new JobFinishListener()
				{
					@Override
					public void jobFinished(int result)
					{
						if (result==JobFinishListener.SUCCESS)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								@Override
								public void run()
								{
									MovieDataDetailsView.create(null, job.getMovieData());
								}
							});
						}
					}
				});
			}
		}
		catch (Exception e)
		{
			log.error("Loading movie info failed.", e);
		}
	}
}
