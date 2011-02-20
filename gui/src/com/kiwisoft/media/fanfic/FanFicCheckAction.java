package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataimport.FanFicData;
import com.kiwisoft.media.dataimport.FanFictionNetLoader;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
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
				FanFicImportUtils.updateFanFic(fanFic, data, loader);
			}
		}
		catch (Exception e)
		{
			GuiUtils.handleThrowable(frame, e);
		}
	}
}
