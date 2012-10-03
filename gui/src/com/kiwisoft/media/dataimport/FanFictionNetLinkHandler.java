package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanFicImportUtils;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.progress.BackgroundJobQueue;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Stefan Stiller
 * @since 15.12.2010
 */
public class FanFictionNetLinkHandler implements LinkHandler
{
	private final static Log log=LogFactory.getLog(FanFictionNetLinkHandler.class);

	@Override
	public void loadLink(final String url)
	{
		log.info("FanFiction.net URL found in clipboard: "+url);
		BackgroundJobQueue.getQueue().addJob(new Job()
		{
			@Override
			public boolean run(ProgressListener progressListener) throws Exception
			{
				ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
				progressSupport.initialize(true, 10, null);
				FanFictionNetLoader loader=new FanFictionNetLoader(url);
				try
				{
					final String baseUrl=loader.getBaseUrl();
					if (baseUrl!=null)
					{
						final FanFicData ficData=loader.getInfo();
						progressSupport.progress(3, false);
						if (ficData!=null)
						{
							Set<FanFic> fanFics=FanFicManager.getInstance().findFanFicsByUrl(baseUrl);
							progressSupport.progress(4, false);
							if (fanFics.isEmpty())
							{
								if (confirm("Load fanfic \""+ficData.getTitle()+"\"?\nDomain: "+StringUtils.formatAsEnumeration(ficData.getDomains(), ", ")))
								{
									FanFicImportUtils.importFanFic(ficData, loader);
								}
							}
							else if (fanFics.size()==1)
							{
								FanFic fanFic=fanFics.iterator().next();
								if (confirm("Update fanfic \""+fanFic.getTitle()+"\"\n with \""+ficData.getTitle()+"\"?"))
								{
									FanFicImportUtils.updateFanFic(fanFic, ficData, loader);
								}
							}
						}
						progressSupport.progress(10, false);
					}
				}
				catch (Exception e)
				{
					log.error("Loading fanfic info failed.", e);
					return false;
				}
				return true;
			}

			@Override
			public String getName()
			{
				return null;
			}

			@Override
			public void dispose() throws IOException
			{
			}
		});
	}

	private boolean confirm(final String message) throws InterruptedException, InvocationTargetException
	{
		final AtomicBoolean result=new AtomicBoolean(false);
		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				result.set(JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION);
			}
		});
		return result.get();
	}
}
