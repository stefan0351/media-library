package com.kiwisoft.media.download;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.net.URL;

import com.kiwisoft.swing.tree.GenericTreeNode;

public class URLNode extends GenericTreeNode<URL>
{
	public URLNode(URL url)
	{
		super(url);
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public String getToolTip()
	{
		return getUserObject().toString();
	}
}
