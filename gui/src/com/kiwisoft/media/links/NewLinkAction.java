package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.Link;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewLinkAction extends SimpleContextAction
{
	public NewLinkAction()
	{
		super(new Class[]{Link.class, Linkable.class}, "Link", Icons.getIcon("link.open"));
	}


	public void actionPerformed(ActionEvent e)
	{
		Object object=getObject();
		if (object instanceof Linkable) LinkDetailsView.create((Linkable)object);
		else if (object instanceof Link) LinkDetailsView.create(((Link)object).getGroup());
	}
}
