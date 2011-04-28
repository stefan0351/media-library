package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataimport.IMDbComLoader;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.progress.JobFinishListener;
import com.kiwisoft.swing.progress.SmallProgressDialog;
import com.kiwisoft.utils.websearch.GoogleSearch;
import com.kiwisoft.utils.websearch.WebSearchDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 * @since 21.02.11
 */
public class LoadMovieFromImdbAction extends ContextAction
{
	private ApplicationFrame frame;

	public LoadMovieFromImdbAction(ApplicationFrame frame)
	{
		super("Download Movie");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GoogleSearch search=new GoogleSearch();
		search.setResultsPerPage(50);
		search.setSite("imdb.com");
		WebSearchDialog dialog=new WebSearchDialog(frame, "Search IMDb.com", search);
		dialog.setVisible(true);
		if (dialog.getResult()!=null)
		{
			IMDbComLoader loader=IMDbComLoader.create(dialog.getResult().getUrl());
			if (loader!=null)
			{
				final LoadMovieJob job=new LoadMovieJob(loader);
				new SmallProgressDialog(frame, job).start(new JobFinishListener()
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
									MovieDataDetailsView.create(frame, job.getMovieData());
								}
							});
						}
					}
				});
			}
		}
	}

}
