package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanFicImportUtils;
import com.kiwisoft.media.fanfic.FanFicManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
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

	public FanFictionNetLinkHandler()
	{
		super();
	}

	@Override
	public void loadLink(String url)
	{
		log.info("FanFiction.net URL found in clipboard: "+url);
		FanFictionNetLoader loader=new FanFictionNetLoader(url);
		try
		{
			final String baseUrl=loader.getBaseUrl();
			if (baseUrl!=null)
			{
				final FanFicData ficData=loader.getInfo();
				if (ficData!=null)
				{
					Set<FanFic> fanFics=FanFicManager.getInstance().findFanFicsByUrl(baseUrl);
					if (fanFics.isEmpty())
					{
						if (confirm("Load fanfic \""+ficData.getTitle()+"\"?\nDomain: "+ficData.getDomain()))
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
			}
		}
		catch (Exception e)
		{
			log.error("Loading fanfic info failed.", e);
		}
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
