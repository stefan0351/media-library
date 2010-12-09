package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataimport.FanFicData;
import com.kiwisoft.media.dataimport.FanFictionNetLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class FanFicCheckAction extends ContextAction
{
	private ApplicationFrame frame;
	private FanFic fanFic;

	public FanFicCheckAction(ApplicationFrame frame)
	{
		super("Update", Icons.getIcon("download"));
		this.frame=frame;
		update(null);
	}

	public void setFanFic(FanFic fanFic)
	{
		this.fanFic=fanFic;
		update(null);
	}

	@Override
	public void update(List objects)
	{
		boolean enabled=false;
		if (fanFic!=null)
		{
			String url=fanFic.getUrl();
			if (url!=null && FanFictionNetLoader.URL_PATTERN.matcher(url).matches()) enabled=true;
		}
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			FanFictionNetLoader loader=new FanFictionNetLoader(fanFic.getUrl());
			final FanFicData data=loader.getInfo();
			if (data!=null)
			{
				List<FanFicPart> parts=fanFic.getParts().elements();
				for (int i=1; i<=data.getChapterCount(); i++)
				{
					final FanFicPart part=i<=parts.size() ? parts.get(i-1) : null;
					if (part!=null)
					{
						File contentFile=part.getContentFile();
						if (contentFile!=null) continue;
					}
					String partContent=loader.getChapter(i);
					boolean result=DBSession.execute(new UpdatePartTransactional(part, data, i, partContent));
					if (!result) return;
				}
				if (data.isComplete())
				{
					DBSession.execute(new Transactional()
					{
						@Override
						public void run() throws Exception
						{
							fanFic.setFinished(true);
						}

						@Override
						public void handleError(Throwable throwable, boolean rollback)
						{
							GuiUtils.handleThrowable(frame, throwable);
						}
					});
				}
			}
		}
		catch (Exception e)
		{
			GuiUtils.handleThrowable(frame, e);
		}
	}

	private class UpdatePartTransactional implements Transactional
	{
		private FanFicPart part;
		private FanFicData data;
		private int chapter;
		private String content;

		public UpdatePartTransactional(FanFicPart part, FanFicData data, int chapter, String content)
		{
			this.part=part;
			this.data=data;
			this.chapter=chapter;
			this.content=content;
		}

		@Override
		public void run() throws Exception
		{
			if (part==null) part=fanFic.createPart();
			if (data.getChapterCount()>1) part.setName(data.getChapters().get(chapter-1));
			part.setType(FanFicPart.TYPE_HTML);
			part.putContent(new ByteArrayInputStream(content.getBytes("UTF-8")), "html", "UTF-8");
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			GuiUtils.handleThrowable(frame, throwable);
		}
	}
}
