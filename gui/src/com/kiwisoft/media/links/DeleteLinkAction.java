package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.util.Set;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class DeleteLinkAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public DeleteLinkAction(ApplicationFrame frame)
	{
		super(new Class[]{Link.class, LinkGroup.class}, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Object object=getObject();
		if (object instanceof Link)
		{
			final Link link=(Link)object;
			if (confirm(link))
			{
				DBSession.execute(new Transactional()
				{
					public void run() throws Exception
					{
						link.getGroup().dropLink(link);
					}

					public void handleError(Throwable throwable, boolean rollback)
					{
						GuiUtils.handleThrowable(frame, throwable);
					}
				});
			}
		}
		else if (object instanceof LinkGroup)
		{
			final LinkGroup linkGroup=(LinkGroup)object;
			if (confirm(linkGroup))
			{
				DBSession.execute(new Transactional()
				{
					public void run() throws Exception
					{
						Set<Show> shows=DBLoader.getInstance().loadSet(Show.class, null, "linkgroup_id=?", linkGroup.getId());
						for (Show show : shows) show.setLinkGroup(null);
						Set<FanDom> fanDoms=DBLoader.getInstance().loadSet(FanDom.class, null, "linkgroup_id=?", linkGroup.getId());
						for (FanDom fanDom : fanDoms) fanDom.setLinkGroup(null);
						LinkGroup parentGroup=linkGroup.getParentGroup();
						if (parentGroup!=null) parentGroup.dropSubGroup(linkGroup);
						else LinkManager.getInstance().dropRootGroup(linkGroup);
					}

					public void handleError(Throwable throwable, boolean rollback)
					{
						GuiUtils.handleThrowable(frame, throwable);
					}
				});
			}
		}
	}

	private boolean confirm(Link link)
	{
		if (link.isUsed())
		{
			showMessageDialog(frame, "The link '"+link.getName()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
			return false;
		}
		return YES_OPTION==showConfirmDialog(frame, "Delete link?", "Confirmation", YES_NO_OPTION, QUESTION_MESSAGE);
	}

	private boolean confirm(LinkGroup group)
	{
		if (group.isUsed())
		{
			showMessageDialog(frame, "The link group '"+group.getName()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
			return false;
		}
		if (!group.getSubGroups().isEmpty() || !group.getLinks().isEmpty())
		{
			return YES_OPTION==showConfirmDialog(frame, "Delete link group?", "Confirmation", YES_NO_OPTION, QUESTION_MESSAGE);
		}
		return true;
	}
}
