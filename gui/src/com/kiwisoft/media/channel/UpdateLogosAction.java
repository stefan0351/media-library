package com.kiwisoft.media.channel;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.dataimport.ImportUtils;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.files.ContentType;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.progress.SmallProgressDialog;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 24.10.2009
 */
public class UpdateLogosAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public UpdateLogosAction(ApplicationFrame frame)
	{
		super(Channel.class, "Update Logos");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Channel> channels=Utils.cast(getObjects());
		if (channels!=null && !channels.isEmpty())
			new SmallProgressDialog(frame, new MyJob(channels)).start();
	}

	private static class MyJob implements Job
	{
		private List<Channel> channels;
		private ProgressSupport progress;

		public MyJob(List<Channel> channels)
		{
			this.channels=channels;
		}

		@Override
		public String getName()
		{
			return "Update Channel Logos";
		}

		@Override
		public boolean run(ProgressListener progressListener) throws Exception
		{
			progress=new ProgressSupport(this, progressListener);
			progress.startStep("Updating logos...");
			progress.initialize(true, channels.size(), null);
			for (final Channel channel : channels)
			{
				if (progress.isStoppedByUser()) return false;
				if (!StringUtils.isEmpty(channel.getTvtvKey()))
				{
					try
					{
						final String logoPath="icons"+File.separator+"tv"+File.separator+"logo"+channel.getTvtvKey()+".jpg";
						final File logoFile=FileUtils.getFile(MediaConfiguration.getRootPath(), logoPath);
						byte[] logoData=ImportUtils.loadUrlBinary("http://www.tvtv.de/tvtv/resource?channelLogo="+channel.getTvtvKey());
						FileUtils.saveToFile(logoData, logoFile);
						final Dimension logoSize=MediaFileUtils.getImageSize(logoFile);
						if (logoSize!=null)
						{
							DBSession.execute(new Transactional()
							{
								@Override
								public void run() throws Exception
								{
									MediaFile newLogo=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
									newLogo.setName(channel.getName()+" - Logo");
									newLogo.setContentType(ContentType.LOGO);
									newLogo.setFile(logoPath);
									newLogo.setWidth(logoSize.width);
									newLogo.setHeight(logoSize.height);
									MediaFile oldLogo=channel.getLogo();
									if (oldLogo!=null) oldLogo.delete();
									channel.setLogo(newLogo);
								}

								@Override
								public void handleError(Throwable throwable, boolean rollback)
								{
									progress.error(throwable);
								}
							});
						}
					}
					catch (Exception e)
					{
						progress.error(e);
					}
				}
				progress.progress();
			}
			return true;
		}

		@Override
		public void dispose() throws IOException
		{
			channels=null;
		}
	}
}
