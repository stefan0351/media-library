package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.LinkManager;

/**
 * @author Stefan Stiller
 */
public class NewLinkGroupAction extends SimpleContextAction
{
	public NewLinkGroupAction()
	{
		super(new Class[]{Link.class, Linkable.class, LinkManager.class}, "Link Group", Icons.getIcon("links"));
	}


	public void actionPerformed(ActionEvent e)
	{
		Object object=getObject();
		if (object instanceof Linkable) LinkGroupDetailsView.openNew((Linkable)object);
		else if (object instanceof Link) LinkGroupDetailsView.openNew(((Link)object).getGroup());
		else if (object instanceof LinkManager) LinkGroupDetailsView.openNew(null);
	}
}
