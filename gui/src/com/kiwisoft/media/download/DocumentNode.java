package com.kiwisoft.media.download;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.kiwisoft.swing.tree.GenericTreeNode;

public class DocumentNode extends GenericTreeNode<WebDocument> implements PropertyChangeListener
{
	public DocumentNode(WebDocument document)
	{
		super(document);
		setNameProperties(WebDocument.STATE);
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), WebDocument.CONTENT_TYPE, this);
		super.installListeners();
	}

	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		if (getUserObject().isParsable())
		{
			nodes.add(new DocumentElementsNode(getUserObject()));
			nodes.add(new DocumentLinksNode(getUserObject()));
		}
		return nodes;
	}

	public boolean isLeaf()
	{
		return !getUserObject().isParsable();
	}

	@Override
	public String getToolTip()
	{
		StringBuilder toolTip=new StringBuilder("<html>");
		toolTip.append("<b>Address:</b> ").append(getUserObject().getURL());
		if (getUserObject().getFile()!=null)
			toolTip.append("<br/><b>File:</b> ").append(getUserObject().getFile().getAbsolutePath());
		toolTip.append("<br/><b>State:</b> ").append(getUserObject().getState());
		if (getUserObject().getState()==WebDocument.FAILED) toolTip.append("<br/><b>Error:</b> ").append(getUserObject().getError());
		toolTip.append("</html>");
		return toolTip.toString();
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		if (WebDocument.CONTENT_TYPE.equals(evt.getPropertyName()))
		{
			boolean parsableOld=ParserFactory.isParsable((String)evt.getOldValue());
			boolean parsableNew=ParserFactory.isParsable((String)evt.getNewValue());
			if (parsableNew!=parsableOld)
			{
				if (isChildrenLoaded())
				{
					if (parsableNew)
					{
						addChild(new DocumentElementsNode(getUserObject()));
						addChild(new DocumentLinksNode(getUserObject()));
					}
					else
					{
						removeAllChildren();
					}
				}
			}
		}
	}
}
