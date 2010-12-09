package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.StringUtils;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class NewFanFicPartAction extends ContextAction
{
	private ApplicationFrame frame;
	private FanFic fanFic;
	private String type;

	public NewFanFicPartAction(ApplicationFrame frame, String name, String type)
	{
		super(name, Icons.getIcon("add"));
		this.frame=frame;
		this.type=type;
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
		setEnabled(fanFic!=null);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				FanFicPart lastPart=null;
				int partCount=fanFic.getParts().size();
				if (partCount>0) lastPart=fanFic.getParts().getLast();
				FanFicPart newPart=fanFic.createPart();
				newPart.setType(type);
				if (lastPart!=null)
				{
					newPart.setName(StringUtils.increase(lastPart.getName()));
				}
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(frame, throwable);
			}
		});
	}
}