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

	public NewFanFicPartAction(ApplicationFrame frame)
	{
		super("New", Icons.getIcon("add"));
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
				if (lastPart!=null)
				{
					newPart.setName(StringUtils.increase(lastPart.getName()));
					newPart.setSource(StringUtils.increase(lastPart.getSource()));
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