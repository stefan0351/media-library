package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Link;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class LinkDetailsAction extends SimpleContextAction
{
	protected LinkDetailsAction()
	{
		super(new Class[]{Link.class, LinkGroup.class}, "Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object object=getObject();
		if (object instanceof Link) LinkDetailsView.create((Link)object);
		else if (object instanceof LinkGroup) LinkGroupDetailsView.openEdit((LinkGroup)object);
	}
}
