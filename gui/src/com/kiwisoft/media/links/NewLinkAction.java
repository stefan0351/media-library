package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.util.List;

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
		super(new Class[]{Link.class, Linkable.class, LinkNode.class, LinkableNode.class, LinkGroupNode.class},
			  "Link", Icons.getIcon("link"));
	}

	@Override
	public void update(List objects)
	{
		super.update(objects);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object object=getObject();
		if (object instanceof Linkable) LinkDetailsView.create((Linkable)object);
		else if (object instanceof Link) LinkDetailsView.create(((Link)object).getGroup());
		else if (object instanceof LinkGroupNode) LinkDetailsView.create(((LinkGroupNode)object).getUserObject());
		else if (object instanceof LinkableNode) LinkDetailsView.create(((LinkableNode)object).getUserObject());
		else if (object instanceof LinkNode) LinkDetailsView.create(((LinkNode)object).getUserObject().getGroup());
	}
}
