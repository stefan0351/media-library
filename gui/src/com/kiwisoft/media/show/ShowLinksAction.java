package com.kiwisoft.media.show;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.media.links.LinksView;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class ShowLinksAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowLinksAction(ApplicationFrame frame)
	{
		super(Linkable.class, "Links", Icons.getIcon("linkgroup"));
		this.frame=frame;
	}

	@Override
	public void update(List objects)
	{
		super.update(objects);
		if (getObject()!=null)
		{
			if (((Linkable)getObject()).getLinkGroup(false)!=null) putValue(Action.NAME, "Links");
			else putValue(Action.NAME, "Create Link Group");
		}
		else putValue(Action.NAME, "Links");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final Linkable linkable=(Linkable) getObject();
		final LinkGroup[] group=new LinkGroup[]{linkable.getLinkGroup(false)};
		if (group[0]==null)
		{
			DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					group[0]=linkable.getLinkGroup(true);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(frame, throwable);
				}
			});
		}
		frame.setCurrentView(new LinksView(group[0]));
	}
}
