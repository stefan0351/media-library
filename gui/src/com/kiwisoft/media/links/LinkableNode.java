package com.kiwisoft.media.links;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.PropertyChangeSource;

/**
 * @author Stefan Stiller
 */
public class LinkableNode extends GenericTreeNode<Linkable>
{
	public LinkableNode(Linkable linkable)
	{
		super(linkable);
		setNameProperties(Show.TITLE);
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener((PropertyChangeSource)getUserObject(), Linkable.LINK_GROUP, new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getNewValue() instanceof LinkGroup)
				{
					GenericTreeNode parentNode=getParent();
					parentNode.removeChild(LinkableNode.this);
				}
			}
		});
		super.installListeners();
	}

	@Override
	public String getFormatVariant()
	{
		return "linkable";
	}
}
